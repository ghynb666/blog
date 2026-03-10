package com.blog.service;

import com.blog.cache.BypassCacheAspect;
import com.blog.cache.BypassCacheEvict;
import com.blog.cache.BypassCacheable;
import com.blog.cache.CacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BypassCacheAspectTest {

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void shouldReadThroughAndHitCacheOnSecondCall() {
        RecordingCacheService cacheService = new RecordingCacheService();
        SampleService service = createProxy(cacheService);

        SamplePayload first = service.load("alpha");
        SamplePayload second = service.load("alpha");

        assertEquals("value-alpha", first.getValue());
        assertEquals("value-alpha", second.getValue());
        assertEquals(1, service.invocationCount());
        assertTrue(cacheService.values.containsKey("sample:alpha"));
        assertEquals(Duration.ofSeconds(120), cacheService.ttls.get("sample:alpha"));
    }

    @Test
    void shouldEvictImmediatelyWhenNoTransactionIsActive() {
        RecordingCacheService cacheService = new RecordingCacheService();
        SampleService service = createProxy(cacheService);

        service.evict("7");

        assertEquals(List.of("sample:7"), cacheService.deletedKeys);
        assertEquals(List.of("sample:list:*"), cacheService.deletedPatterns);
    }

    @Test
    void shouldEvictAfterCommitWhenTransactionSynchronizationIsActive() {
        RecordingCacheService cacheService = new RecordingCacheService();
        SampleService service = createProxy(cacheService);
        TransactionSynchronizationManager.initSynchronization();

        service.evict("9");

        assertTrue(cacheService.deletedKeys.isEmpty());
        assertTrue(cacheService.deletedPatterns.isEmpty());

        for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
            synchronization.afterCommit();
        }

        assertEquals(List.of("sample:9"), cacheService.deletedKeys);
        assertEquals(List.of("sample:list:*"), cacheService.deletedPatterns);
    }

    private SampleService createProxy(RecordingCacheService cacheService) {
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(new SampleServiceImpl());
        proxyFactory.addAspect(new BypassCacheAspect(cacheService, new ObjectMapper()));
        return (SampleService) proxyFactory.getProxy();
    }

    interface SampleService {

        SamplePayload load(String id);

        void evict(String id);

        int invocationCount();
    }

    static class SampleServiceImpl implements SampleService {

        private int invocationCount;

        @Override
        @BypassCacheable(key = "'sample:' + #id", ttlSeconds = 120)
        public SamplePayload load(String id) {
            invocationCount++;
            return new SamplePayload("value-" + id);
        }

        @Override
        @BypassCacheEvict(keys = {"'sample:' + #id"}, patterns = {"'sample:list:*'"})
        public void evict(String id) {
        }

        @Override
        public int invocationCount() {
            return invocationCount;
        }
    }

    static class SamplePayload {

        private String value;

        public SamplePayload() {
        }

        public SamplePayload(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    static class RecordingCacheService implements CacheService {

        private final Map<String, String> values = new HashMap<>();
        private final Map<String, Duration> ttls = new HashMap<>();
        private final List<String> deletedKeys = new ArrayList<>();
        private final List<String> deletedPatterns = new ArrayList<>();

        @Override
        public Optional<String> get(String key) {
            return Optional.ofNullable(values.get(key));
        }

        @Override
        public void set(String key, String value, Duration ttl) {
            values.put(key, value);
            ttls.put(key, ttl);
        }

        @Override
        public void delete(String key) {
            deletedKeys.add(key);
            values.remove(key);
        }

        @Override
        public void deletePattern(String pattern) {
            deletedPatterns.add(pattern);
        }
    }
}
