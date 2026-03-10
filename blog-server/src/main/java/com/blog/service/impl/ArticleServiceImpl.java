package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.cache.BypassCacheEvict;
import com.blog.cache.BypassCacheable;
import com.blog.common.AppException;
import com.blog.common.ErrorCode;
import com.blog.dto.ArticleDTO;
import com.blog.entity.Article;
import com.blog.entity.ArticleTag;
import com.blog.entity.Category;
import com.blog.entity.Tag;
import com.blog.event.publisher.ArticleDomainEventPublisher;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.mapper.TagMapper;
import com.blog.service.ArticleService;
import com.blog.service.ArticleInteractionService;
import com.blog.vo.ArchiveVO;
import com.blog.vo.ArticleDetailVO;
import com.blog.vo.ArticleListVO;
import com.blog.vo.ArticleVO;
import com.blog.vo.TagVO;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private ArticleTagMapper articleTagMapper;

    @Resource
    private ArticleDomainEventPublisher articleDomainEventPublisher;

    @Resource
    private ArticleInteractionService articleInteractionService;

    @Resource
    @Lazy
    private ArticleService self;

    @Override
    @Transactional
    @BypassCacheEvict(
            patterns = {
                    "T(com.blog.cache.CacheKeys).articleListPattern()",
                    "T(com.blog.cache.CacheKeys).articleDetailPattern()",
                    "T(com.blog.cache.CacheKeys).feedRssPattern()",
                    "T(com.blog.cache.CacheKeys).feedSitemapPattern()"
            },
            keys = {
                    "T(com.blog.cache.CacheKeys).archiveList()"
            }
    )
    public void create(ArticleDTO dto) {
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        article.setViewCount(0L);
        articleMapper.insert(article);
        saveArticleTags(article.getId(), dto.getTagIds());
        articleDomainEventPublisher.publishArticlePublished(null, article.getId(), article.getTitle());
    }

    @Override
    @Transactional
    @BypassCacheEvict(
            patterns = {
                    "T(com.blog.cache.CacheKeys).articleListPattern()",
                    "T(com.blog.cache.CacheKeys).articleDetailPattern()",
                    "T(com.blog.cache.CacheKeys).feedRssPattern()",
                    "T(com.blog.cache.CacheKeys).feedSitemapPattern()"
            },
            keys = {
                    "T(com.blog.cache.CacheKeys).archiveList()"
            }
    )
    public void update(Long id, ArticleDTO dto) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new AppException(ErrorCode.ARTICLE_NOT_FOUND);
        }
        BeanUtils.copyProperties(dto, article);
        articleMapper.updateById(article);
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id));
        saveArticleTags(id, dto.getTagIds());
        articleDomainEventPublisher.publishArticleUpdated(null, id, article.getTitle());
    }

    @Override
    @Transactional
    @BypassCacheEvict(
            patterns = {
                    "T(com.blog.cache.CacheKeys).articleListPattern()",
                    "T(com.blog.cache.CacheKeys).articleDetailPattern()",
                    "T(com.blog.cache.CacheKeys).feedRssPattern()",
                    "T(com.blog.cache.CacheKeys).feedSitemapPattern()"
            },
            keys = {
                    "T(com.blog.cache.CacheKeys).archiveList()"
            }
    )
    public void delete(Long id) {
        articleMapper.deleteById(id);
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id));
    }

    @Override
    public Page<ArticleListVO> list(Integer page, Integer size, Integer status, Long categoryId) {
        Page<Article> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Article::getStatus, status);
        }
        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Article::getCreatedAt);
        Page<Article> articlePage = articleMapper.selectPage(pageParam, wrapper);
        Page<ArticleListVO> voPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(), articlePage.getTotal());
        voPage.setRecords(articlePage.getRecords().stream().map(this::toListVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public ArticleVO detail(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new AppException(ErrorCode.ARTICLE_NOT_FOUND);
        }
        ArticleVO vo = new ArticleVO();
        BeanUtils.copyProperties(article, vo);
        Category category = categoryMapper.selectById(article.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        List<ArticleTag> articleTags = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id));
        if (!CollectionUtils.isEmpty(articleTags)) {
            List<Long> tagIds = articleTags.stream().map(ArticleTag::getTagId).collect(Collectors.toList());
            List<Tag> tags = tagMapper.selectBatchIds(tagIds);
            vo.setTags(tags.stream().map(this::toTagVO).collect(Collectors.toList()));
        }
        return vo;
    }

    @Override
    @BypassCacheable(
            key = "T(com.blog.cache.CacheKeys).articleList(#page, #size, #keyword, #orderBy, #categoryId, #tagIds)",
            ttlExpression = "T(com.blog.cache.CacheKeys).articleListTtl(#orderBy)"
    )
    public Page<ArticleListVO> frontList(Integer page, Integer size, String keyword, String orderBy, Long categoryId, List<Long> tagIds) {
        List<Long> articleIdsByTags = null;
        if (!CollectionUtils.isEmpty(tagIds)) {
            List<ArticleTag> articleTags = articleTagMapper.selectList(
                    new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getTagId, tagIds)
            );
            if (CollectionUtils.isEmpty(articleTags)) {
                Page<ArticleListVO> emptyPage = new Page<>(page, size, 0);
                emptyPage.setRecords(new ArrayList<>());
                return emptyPage;
            }
            Map<Long, Long> articleTagCount = articleTags.stream()
                    .collect(Collectors.groupingBy(ArticleTag::getArticleId, Collectors.counting()));
            articleIdsByTags = articleTagCount.entrySet().stream()
                    .filter(e -> e.getValue() == tagIds.size())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(articleIdsByTags)) {
                Page<ArticleListVO> emptyPage = new Page<>(page, size, 0);
                emptyPage.setRecords(new ArrayList<>());
                return emptyPage;
            }
        }

        Page<Article> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Article::getTitle, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }
        if (articleIdsByTags != null) {
            wrapper.in(Article::getId, articleIdsByTags);
        }
        if ("viewCount".equals(orderBy)) {
            wrapper.orderByDesc(Article::getViewCount);
        } else {
            wrapper.orderByDesc(Article::getCreatedAt);
        }

        Page<Article> articlePage = articleMapper.selectPage(pageParam, wrapper);
        Page<ArticleListVO> voPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(), articlePage.getTotal());
        voPage.setRecords(articlePage.getRecords().stream().map(this::toListVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional
    public ArticleDetailVO frontDetail(Long id, HttpServletRequest request) {
        ArticleDetailVO vo = self.getFrontDetailSnapshot(id);

        Long currentUserId = null;
        try {
            if (StpUtil.isLogin()) {
                currentUserId = StpUtil.getLoginIdAsLong();
            }
        } catch (Exception ignored) {
        }

        String ip = getClientIp(request);
        articleDomainEventPublisher.publishArticleViewed(id, ip, request.getHeader("User-Agent"));
        var interaction = articleInteractionService.getInteraction(id, currentUserId);
        vo.setCommentCount(interaction.getCommentCount());
        vo.setLikeCount(interaction.getLikeCount());
        vo.setLiked(interaction.getLiked());
        return vo;
    }

    @Override
    @BypassCacheable(
            key = "T(com.blog.cache.CacheKeys).articleDetail(#id)",
            ttlSeconds = 60
    )
    public ArticleDetailVO getFrontDetailSnapshot(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getStatus() != 1) {
            throw new AppException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        ArticleDetailVO vo = new ArticleDetailVO();
        BeanUtils.copyProperties(article, vo);
        Category category = categoryMapper.selectById(article.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        List<ArticleTag> articleTags = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id));
        if (!CollectionUtils.isEmpty(articleTags)) {
            List<Long> tagIds = articleTags.stream().map(ArticleTag::getTagId).collect(Collectors.toList());
            List<Tag> tags = tagMapper.selectBatchIds(tagIds);
            vo.setTags(tags.stream().map(this::toTagVO).collect(Collectors.toList()));
        }
        vo.setCreatedAt(article.getCreatedAt());
        vo.setUpdatedAt(article.getUpdatedAt());
        return vo;
    }

    @Override
    @BypassCacheable(
            key = "T(com.blog.cache.CacheKeys).archiveList()",
            ttlSeconds = 600
    )
    public List<ArchiveVO> archives() {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1).orderByDesc(Article::getCreatedAt);
        List<Article> articles = articleMapper.selectList(wrapper);
        Map<Integer, Map<Integer, List<Article>>> grouped = articles.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCreatedAt().getYear(),
                        Collectors.groupingBy(a -> a.getCreatedAt().getMonthValue())
                ));
        List<ArchiveVO> result = new ArrayList<>();
        grouped.forEach((year, monthMap) -> {
            monthMap.forEach((month, articleList) -> {
                ArchiveVO vo = new ArchiveVO();
                vo.setYear(year);
                vo.setMonth(month);
                vo.setArticles(articleList.stream().map(this::toSimpleVO).collect(Collectors.toList()));
                result.add(vo);
            });
        });
        result.sort((a, b) -> {
            int yearCompare = b.getYear().compareTo(a.getYear());
            if (yearCompare != 0) {
                return yearCompare;
            }
            return b.getMonth().compareTo(a.getMonth());
        });
        return result;
    }

    @Override
    public void incrementViewCount(Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            articleMapper.updateById(article);
        }
    }

    private void saveArticleTags(Long articleId, List<Long> tagIds) {
        if (!CollectionUtils.isEmpty(tagIds)) {
            for (Long tagId : tagIds) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(articleId);
                articleTag.setTagId(tagId);
                articleTagMapper.insert(articleTag);
            }
        }
    }

    private ArticleListVO toListVO(Article article) {
        ArticleListVO vo = new ArticleListVO();
        BeanUtils.copyProperties(article, vo);
        Category category = categoryMapper.selectById(article.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        return vo;
    }

    private TagVO toTagVO(Tag tag) {
        TagVO vo = new TagVO();
        BeanUtils.copyProperties(tag, vo);
        return vo;
    }

    private ArchiveVO.ArticleSimpleVO toSimpleVO(Article article) {
        ArchiveVO.ArticleSimpleVO vo = new ArchiveVO.ArticleSimpleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSummary(article.getSummary());
        vo.setCreatedAt(article.getCreatedAt());
        return vo;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
