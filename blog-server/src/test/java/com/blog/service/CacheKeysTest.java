package com.blog.service;

import com.blog.cache.CacheKeys;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CacheKeysTest {

    @Test
    void shouldBuildStableAdminArticleListKey() {
        String key = CacheKeys.adminArticleList(1, 10, 1, 3L);

        assertEquals("admin:article:list:p=1:s=10:status=1:cid=3", key);
    }

    @Test
    void shouldDistinguishAdminArticleListKeysByQueryParams() {
        String base = CacheKeys.adminArticleList(1, 10, 1, 3L);

        assertNotEquals(base, CacheKeys.adminArticleList(2, 10, 1, 3L));
        assertNotEquals(base, CacheKeys.adminArticleList(1, 20, 1, 3L));
        assertNotEquals(base, CacheKeys.adminArticleList(1, 10, 0, 3L));
        assertNotEquals(base, CacheKeys.adminArticleList(1, 10, 1, 4L));
    }

    @Test
    void shouldBuildAdminArticlePatternsAndDetailKey() {
        assertEquals("admin:article:list:*", CacheKeys.adminArticleListPattern());
        assertEquals("admin:article:detail:42", CacheKeys.adminArticleDetail(42L));
        assertEquals("admin:article:detail:*", CacheKeys.adminArticleDetailPattern());
    }
}
