package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.cache.BypassCacheEvict;
import com.blog.cache.BypassCacheable;
import com.blog.common.AppException;
import com.blog.common.ErrorCode;
import com.blog.entity.GrowthEvent;
import com.blog.mapper.GrowthEventMapper;
import com.blog.service.GrowthEventService;
import com.blog.vo.AnalyticsOverviewVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GrowthEventServiceImpl implements GrowthEventService {

    private final GrowthEventMapper growthEventMapper;

    public GrowthEventServiceImpl(GrowthEventMapper growthEventMapper) {
        this.growthEventMapper = growthEventMapper;
    }

    @Override
    @BypassCacheEvict(
            patterns = {
                    "T(com.blog.cache.CacheKeys).analyticsOverviewPattern()"
            }
    )
    public void record(String eventType, Long userId, Long articleId, String eventData) {
        GrowthEvent event = new GrowthEvent();
        event.setEventType(eventType);
        event.setUserId(userId);
        event.setArticleId(articleId);
        event.setEventData(eventData);
        growthEventMapper.insert(event);
    }

    @Override
    public void assertCooldown(String eventType, Long userId, int seconds, ErrorCode errorCode) {
        if (userId == null) {
            return;
        }
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(seconds);
        Long count = growthEventMapper.selectCount(new LambdaQueryWrapper<GrowthEvent>()
                .eq(GrowthEvent::getEventType, eventType)
                .eq(GrowthEvent::getUserId, userId)
                .ge(GrowthEvent::getCreatedAt, threshold));
        if (count != null && count > 0) {
            throw new AppException(errorCode);
        }
    }

    @Override
    @BypassCacheable(
            key = "T(com.blog.cache.CacheKeys).analyticsOverview(#days)",
            ttlSeconds = 60
    )
    public AnalyticsOverviewVO buildOverview(int days) {
        List<String> types = List.of(
                "user_registered",
                "user_logged_in",
                "article_commented",
                "article_liked",
                "subscription_created"
        );
        List<GrowthEvent> events = growthEventMapper.selectList(new LambdaQueryWrapper<GrowthEvent>()
                .in(GrowthEvent::getEventType, types)
                .ge(GrowthEvent::getCreatedAt, LocalDateTime.now().minusDays(days - 1L))
                .orderByAsc(GrowthEvent::getCreatedAt));

        AnalyticsOverviewVO overview = new AnalyticsOverviewVO();
        AnalyticsOverviewVO.Summary summary = new AnalyticsOverviewVO.Summary();
        summary.setRegistrations(countByType(events, "user_registered"));
        summary.setLogins(countByType(events, "user_logged_in"));
        summary.setComments(countByType(events, "article_commented"));
        summary.setLikes(countByType(events, "article_liked"));
        summary.setSubscriptions(countByType(events, "subscription_created"));
        overview.setSummary(summary);

        Map<LocalDate, AnalyticsOverviewVO.DailyMetric> metrics = new LinkedHashMap<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            AnalyticsOverviewVO.DailyMetric metric = new AnalyticsOverviewVO.DailyMetric();
            metric.setDate(date.toString());
            metrics.put(date, metric);
        }

        for (GrowthEvent event : events) {
            LocalDate date = event.getCreatedAt().toLocalDate();
            AnalyticsOverviewVO.DailyMetric metric = metrics.get(date);
            if (metric == null) {
                continue;
            }
            switch (event.getEventType()) {
                case "user_registered":
                    metric.setRegistrations(metric.getRegistrations() + 1);
                    break;
                case "user_logged_in":
                    metric.setLogins(metric.getLogins() + 1);
                    break;
                case "article_commented":
                    metric.setComments(metric.getComments() + 1);
                    break;
                case "article_liked":
                    metric.setLikes(metric.getLikes() + 1);
                    break;
                case "subscription_created":
                    metric.setSubscriptions(metric.getSubscriptions() + 1);
                    break;
                default:
                    break;
            }
        }

        overview.setDailyMetrics(new ArrayList<>(metrics.values()));
        return overview;
    }

    private long countByType(List<GrowthEvent> events, String eventType) {
        return events.stream().filter(event -> eventType.equals(event.getEventType())).count();
    }
}
