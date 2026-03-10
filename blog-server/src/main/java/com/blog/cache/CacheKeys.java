package com.blog.cache;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CacheKeys {

    private CacheKeys() {
    }

    public static String articleList(Integer page, Integer size, String keyword, String orderBy, Long categoryId, List<Long> tagIds) {
        return "article:list:p=" + safe(page)
                + ":s=" + safe(size)
                + ":kw=" + safe(keyword)
                + ":ob=" + safe(orderBy)
                + ":cid=" + safe(categoryId)
                + ":tags=" + normalizeTagIds(tagIds);
    }

    public static String adminArticleList(Integer page, Integer size, Integer status, Long categoryId) {
        return "admin:article:list:p=" + safe(page)
                + ":s=" + safe(size)
                + ":status=" + safe(status)
                + ":cid=" + safe(categoryId);
    }

    public static String adminArticleListPattern() {
        return "admin:article:list:*";
    }

    public static String adminArticleDetail(Long id) {
        return "admin:article:detail:" + id;
    }

    public static String adminArticleDetailPattern() {
        return "admin:article:detail:*";
    }

    public static long articleListTtl(String orderBy) {
        return "viewCount".equals(orderBy) ? 60L : 300L;
    }

    public static String articleListPattern() {
        return "article:list:*";
    }

    public static String articleDetail(Long id) {
        return "article:detail:" + id;
    }

    public static String articleDetailPattern() {
        return "article:detail:*";
    }

    public static String categoryList() {
        return "category:list";
    }

    public static String tagList() {
        return "tag:list";
    }

    public static String archiveList() {
        return "archive:list";
    }

    public static String commentTree(Long articleId) {
        return "comment:tree:" + articleId;
    }

    public static String interactionPublic(Long articleId) {
        return "interaction:public:" + articleId;
    }

    public static String feedRss(HttpServletRequest request) {
        return "feed:rss:" + sanitize(baseUrl(request));
    }

    public static String feedRssPattern() {
        return "feed:rss:*";
    }

    public static String feedSitemap(HttpServletRequest request) {
        return "feed:sitemap:" + sanitize(baseUrl(request));
    }

    public static String feedSitemapPattern() {
        return "feed:sitemap:*";
    }

    public static String analyticsOverview(int days) {
        return "analytics:overview:" + Math.max(days, 1);
    }

    public static String analyticsOverviewPattern() {
        return "analytics:overview:*";
    }

    public static String baseUrl(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(request.getScheme()).append("://").append(request.getServerName());
        boolean isDefaultPort = ("http".equalsIgnoreCase(request.getScheme()) && request.getServerPort() == 80)
                || ("https".equalsIgnoreCase(request.getScheme()) && request.getServerPort() == 443);
        if (!isDefaultPort) {
            builder.append(":").append(request.getServerPort());
        }
        return builder.toString();
    }

    private static String normalizeTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return "_";
        }
        return tagIds.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.naturalOrder())
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private static String sanitize(String value) {
        return value.replace("://", "_").replace(':', '_').replace('/', '_');
    }

    private static String safe(Object value) {
        return value == null ? "_" : String.valueOf(value).replace(':', '_');
    }
}
