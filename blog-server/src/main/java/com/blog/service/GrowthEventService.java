package com.blog.service;

import com.blog.vo.AnalyticsOverviewVO;

public interface GrowthEventService {

    void record(String eventType, Long userId, Long articleId, String eventData);

    void assertCooldown(String eventType, Long userId, int seconds, com.blog.common.ErrorCode errorCode);

    AnalyticsOverviewVO buildOverview(int days);
}
