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
                .orderByDesc(ArticleComment::getCreatedAt));
        List<Long> userIds = comments.stream().map(ArticleComment::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> users = userIds.isEmpty()
                ? Map.of()
                : userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(User::getId, user -> user));
        return comments.stream().map(comment -> toCommentVO(comment, users.get(comment.getUserId()))).collect(Collectors.toList());
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
        comment.setContent(dto.getContent().trim());
        comment.setStatus(1);
        articleCommentMapper.insert(comment);
        growthEventService.record("article_commented", userId, articleId, "{\"source\":\"article-detail\"}");

        User user = userMapper.selectById(userId);
        return toCommentVO(comment, user);
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

    private ArticleCommentVO toCommentVO(ArticleComment comment, User user) {
        ArticleCommentVO vo = new ArticleCommentVO();
        vo.setId(comment.getId());
        vo.setArticleId(comment.getArticleId());
        vo.setContent(comment.getContent());
        vo.setStatus(comment.getStatus());
        vo.setCreatedAt(comment.getCreatedAt());
        if (user != null) {
            ArticleCommentVO.UserSummary summary = new ArticleCommentVO.UserSummary();
            summary.setId(user.getId());
            summary.setUsername(user.getUsername());
            summary.setNickname(user.getNickname());
            summary.setAvatar(user.getAvatar());
            vo.setUser(summary);
        }
        return vo;
    }
}
