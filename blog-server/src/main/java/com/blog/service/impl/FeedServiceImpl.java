package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.cache.BypassCacheable;
import com.blog.cache.CacheKeys;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.entity.Tag;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.mapper.TagMapper;
import com.blog.service.FeedService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FeedServiceImpl implements FeedService {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    public FeedServiceImpl(ArticleMapper articleMapper, CategoryMapper categoryMapper, TagMapper tagMapper) {
        this.articleMapper = articleMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
    }

    @Override
    @BypassCacheable(
            key = "T(com.blog.cache.CacheKeys).feedSitemap(#request)",
            ttlSeconds = 1800
    )
    public String buildSitemap(HttpServletRequest request) {
        String baseUrl = buildBaseUrl(request);
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
        appendUrl(xml, baseUrl + "/", null);
        appendUrl(xml, baseUrl + "/archives", null);
        List<Article> articles = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1)
                .orderByDesc(Article::getUpdatedAt));
        for (Article article : articles) {
            appendUrl(xml, baseUrl + "/article/" + article.getId(), article.getUpdatedAt());
        }
        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
        for (Category category : categories) {
            appendUrl(xml, baseUrl + "/category/" + category.getId(), category.getUpdatedAt());
        }
        List<Tag> tags = tagMapper.selectList(new LambdaQueryWrapper<Tag>().orderByDesc(Tag::getUpdatedAt));
        for (Tag tag : tags) {
            appendUrl(xml, baseUrl + "/tag/" + tag.getId(), tag.getUpdatedAt());
        }
        xml.append("</urlset>");
        return xml.toString();
    }

    @Override
    @BypassCacheable(
            key = "T(com.blog.cache.CacheKeys).feedRss(#request)",
            ttlSeconds = 600
    )
    public String buildRss(HttpServletRequest request) {
        String baseUrl = buildBaseUrl(request);
        List<Article> articles = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1)
                .orderByDesc(Article::getCreatedAt)
                .last("limit 20"));
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<rss version=\"2.0\"><channel>");
        xml.append("<title>Blog RSS</title>");
        xml.append("<link>").append(baseUrl).append("</link>");
        xml.append("<description>Latest posts from Blog</description>");
        for (Article article : articles) {
            xml.append("<item>");
            xml.append("<title>").append(escapeXml(article.getTitle())).append("</title>");
            xml.append("<link>").append(baseUrl).append("/article/").append(article.getId()).append("</link>");
            xml.append("<guid>").append(baseUrl).append("/article/").append(article.getId()).append("</guid>");
            xml.append("<description>").append(escapeXml(article.getSummary())).append("</description>");
            if (article.getCreatedAt() != null) {
                xml.append("<pubDate>")
                        .append(article.getCreatedAt().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                        .append("</pubDate>");
            }
            xml.append("</item>");
        }
        xml.append("</channel></rss>");
        return xml.toString();
    }

    private void appendUrl(StringBuilder xml, String location, java.time.LocalDateTime lastModified) {
        xml.append("<url><loc>").append(location).append("</loc>");
        if (lastModified != null) {
            xml.append("<lastmod>").append(lastModified.toLocalDate()).append("</lastmod>");
        }
        xml.append("</url>");
    }

    private String buildBaseUrl(HttpServletRequest request) {
        return CacheKeys.baseUrl(request);
    }

    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
