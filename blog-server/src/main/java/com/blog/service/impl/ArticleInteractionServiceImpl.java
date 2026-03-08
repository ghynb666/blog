package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.AppException;
import com.blog.common.ErrorCode;
import com.blog.dto.CommentCreateDTO;
import com.blog.entity.Article;
import com.blog.entity.ArticleComment;
import com.blog.entity.ArticleLike;
import com.blog.entity.User;
import com.blog.mapper.ArticleCommentMapper;
import com.blog.mapper.ArticleLikeMapper;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.UserMapper;
import com.blog.service.ArticleInteractionService;
import com.blog.service.GrowthEventService;
import com.blog.vo.ArticleCommentVO;
import com.blog.vo.ArticleInteractionVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArticleInteractionServiceImpl implements ArticleInteractionService {

    private final ArticleMapper articleMapper;
    private final ArticleCommentMapper articleCommentMapper;
    private final ArticleLikeMapper articleLikeMapper;
    private final UserMapper userMapper;
    private final GrowthEventService growthEventService;

    public ArticleInteractionServiceImpl(
            ArticleMapper articleMapper,
            ArticleCommentMapper articleCommentMapper,
            ArticleLikeMapper articleLikeMapper,
            UserMapper userMapper,
            GrowthEventService growthEventService
    ) {
        this.articleMapper = articleMapper;
        this.articleCommentMapper = articleCommentMapper;
        this.articleLikeMapper = articleLikeMapper;
        this.userMapper = userMapper;
        this.growthEventService = growthEventService;
    }

    @Override
    public List<ArticleCommentVO> listComments(Long articleId) {
        assertArticleAvailable(articleId);
        List<ArticleComment> comments = articleCommentMapper.selectList(new LambdaQueryWrapper<ArticleComment>()
                .eq(ArticleComment::getArticleId, articleId)
                .eq(ArticleComment::getStatus, 1)
                .orderByAsc(ArticleComment::getCreatedAt));
        List<Long> userIds = comments.stream().map(ArticleComment::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> users = userIds.isEmpty()
                ? Map.of()
                : userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(User::getId, user -> user));
        Map<Long, ArticleComment> commentMap = comments.stream().collect(Collectors.toMap(ArticleComment::getId, comment -> comment));
        Map<Long, ArticleCommentVO> voMap = comments.stream().collect(Collectors.toMap(
                ArticleComment::getId,
                comment -> toCommentVO(comment, users.get(comment.getUserId()), resolveReplyUser(comment, commentMap, users))
        ));

        List<ArticleCommentVO> roots = comments.stream()
                .map(comment -> voMap.get(comment.getId()))
                .filter(vo -> {
                    if (vo.getParentId() == null) {
                        return true;
                    }
                    ArticleCommentVO parent = voMap.get(vo.getParentId());
                    if (parent == null) {
                        vo.setParentId(null);
                        return true;
                    }
                    parent.getChildren().add(vo);
                    return false;
                })
                .sorted(Comparator.comparing(ArticleCommentVO::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());
        sortChildren(roots);
        return roots;
    }

    @Override
    @Transactional
    public ArticleCommentVO createComment(Long articleId, Long userId, CommentCreateDTO dto) {
        assertArticleAvailable(articleId);
        growthEventService.assertCooldown("article_commented", userId, 15, ErrorCode.COMMENT_TOO_FREQUENT);
        if (!StringUtils.hasText(dto.getContent())) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        ArticleComment comment = new ArticleComment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setParentId(resolveParentId(articleId, dto.getParentId()));
        comment.setContent(dto.getContent().trim());
        comment.setStatus(1);
        articleCommentMapper.insert(comment);
        growthEventService.record("article_commented", userId, articleId, "{\"source\":\"article-detail\"}");

        User user = userMapper.selectById(userId);
        User replyToUser = null;
        if (comment.getParentId() != null) {
            ArticleComment parentComment = articleCommentMapper.selectById(comment.getParentId());
            if (parentComment != null) {
                replyToUser = userMapper.selectById(parentComment.getUserId());
            }
        }
        return toCommentVO(comment, user, replyToUser);
    }

    @Override
    @Transactional
    public ArticleInteractionVO toggleLike(Long articleId, Long userId) {
        assertArticleAvailable(articleId);
        ArticleLike existed = articleLikeMapper.selectOne(new LambdaQueryWrapper<ArticleLike>()
                .eq(ArticleLike::getArticleId, articleId)
                .eq(ArticleLike::getUserId, userId)
                .last("limit 1"));
        boolean liked;
        if (existed == null) {
            ArticleLike articleLike = new ArticleLike();
            articleLike.setArticleId(articleId);
            articleLike.setUserId(userId);
            articleLikeMapper.insert(articleLike);
            growthEventService.record("article_liked", userId, articleId, "{\"source\":\"article-detail\"}");
            liked = true;
        } else {
            articleLikeMapper.deleteById(existed.getId());
            liked = false;
        }
        return buildInteraction(articleId, userId, liked);
    }

    @Override
    public ArticleInteractionVO getInteraction(Long articleId, Long userId) {
        assertArticleAvailable(articleId);
        return buildInteraction(articleId, userId, hasLiked(articleId, userId));
    }

    private void assertArticleAvailable(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null || article.getStatus() == null || article.getStatus() != 1) {
            throw new AppException(ErrorCode.ARTICLE_NOT_FOUND);
        }
    }

    private boolean hasLiked(Long articleId, Long userId) {
        if (userId == null) {
            return false;
        }
        Long count = articleLikeMapper.selectCount(new LambdaQueryWrapper<ArticleLike>()
                .eq(ArticleLike::getArticleId, articleId)
                .eq(ArticleLike::getUserId, userId));
        return count != null && count > 0;
    }

    private ArticleInteractionVO buildInteraction(Long articleId, Long userId, boolean liked) {
        ArticleInteractionVO vo = new ArticleInteractionVO();
        vo.setArticleId(articleId);
        vo.setCommentCount(articleCommentMapper.selectCount(new LambdaQueryWrapper<ArticleComment>()
                .eq(ArticleComment::getArticleId, articleId)
                .eq(ArticleComment::getStatus, 1)));
        vo.setLikeCount(articleLikeMapper.selectCount(new LambdaQueryWrapper<ArticleLike>()
                .eq(ArticleLike::getArticleId, articleId)));
        vo.setLiked(liked || hasLiked(articleId, userId));
        return vo;
    }

    private Long resolveParentId(Long articleId, Long parentId) {
        if (parentId == null) {
            return null;
        }
        ArticleComment parentComment = articleCommentMapper.selectById(parentId);
        if (parentComment == null || parentComment.getStatus() == null || parentComment.getStatus() != 1) {
            throw new AppException(ErrorCode.COMMENT_NOT_FOUND);
        }
        if (!articleId.equals(parentComment.getArticleId())) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        return parentComment.getId();
    }

    private User resolveReplyUser(ArticleComment comment, Map<Long, ArticleComment> commentMap, Map<Long, User> users) {
        if (comment.getParentId() == null) {
            return null;
        }
        ArticleComment parentComment = commentMap.get(comment.getParentId());
        if (parentComment == null) {
            return null;
        }
        return users.get(parentComment.getUserId());
    }

    private void sortChildren(List<ArticleCommentVO> comments) {
        comments.forEach(comment -> {
            comment.getChildren().sort(Comparator.comparing(ArticleCommentVO::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())));
            sortChildren(comment.getChildren());
        });
    }

    private ArticleCommentVO toCommentVO(ArticleComment comment, User user, User replyToUser) {
        ArticleCommentVO vo = new ArticleCommentVO();
        vo.setId(comment.getId());
        vo.setArticleId(comment.getArticleId());
        vo.setParentId(comment.getParentId());
        vo.setContent(comment.getContent());
        vo.setStatus(comment.getStatus());
        vo.setCreatedAt(comment.getCreatedAt());
        if (user != null) {
            vo.setUser(toUserSummary(user));
        }
        if (replyToUser != null) {
            vo.setReplyToUser(toUserSummary(replyToUser));
        }
        return vo;
    }

    private ArticleCommentVO.UserSummary toUserSummary(User user) {
        ArticleCommentVO.UserSummary summary = new ArticleCommentVO.UserSummary();
        summary.setId(user.getId());
        summary.setUsername(user.getUsername());
        summary.setNickname(user.getNickname());
        summary.setAvatar(user.getAvatar());
        return summary;
    }
}
