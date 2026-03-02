package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.dto.ArticleDTO;
import com.blog.entity.Article;
import com.blog.entity.ArticleTag;
import com.blog.entity.Category;
import com.blog.entity.Tag;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.mapper.TagMapper;
import com.blog.service.ArticleService;
import com.blog.service.VisitLogService;
import com.blog.vo.ArchiveVO;
import com.blog.vo.ArticleDetailVO;
import com.blog.vo.ArticleListVO;
import com.blog.vo.ArticleVO;
import com.blog.vo.TagVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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
    private VisitLogService visitLogService;

    @Override
    @Transactional
    public void create(ArticleDTO dto) {
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        article.setViewCount(0L);
        articleMapper.insert(article);
        saveArticleTags(article.getId(), dto.getTagIds());
    }

    @Override
    @Transactional
    public void update(Long id, ArticleDTO dto) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        BeanUtils.copyProperties(dto, article);
        articleMapper.updateById(article);
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id));
        saveArticleTags(id, dto.getTagIds());
    }

    @Override
    @Transactional
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
            throw new RuntimeException("文章不存在");
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

    @Override
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
        Article article = articleMapper.selectById(id);
        if (article == null || article.getStatus() != 1) {
            throw new RuntimeException("文章不存在");
        }
        String ip = getClientIp(request);
        if (visitLogService.canCount(id, ip)) {
            article.setViewCount(article.getViewCount() + 1);
            articleMapper.updateById(article);
            visitLogService.recordVisit(id, ip, request.getHeader("User-Agent"));
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
        return vo;
    }

    @Override
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
            if (yearCompare != 0) return yearCompare;
            return b.getMonth().compareTo(a.getMonth());
        });
        return result;
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
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    @Override
    public void incrementViewCount(Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            articleMapper.updateById(article);
        }
    }
}
