package com.blog.vo;

import lombok.Data;

import java.util.List;

@Data
public class AnalyticsOverviewVO {

    private Summary summary;

    private List<DailyMetric> dailyMetrics;

    @Data
    public static class Summary {
        private Long registrations;
        private Long logins;
        private Long comments;
        private Long likes;
        private Long subscriptions;
    }

    @Data
    public static class DailyMetric {
        private String date;
        private Long registrations = 0L;
        private Long logins = 0L;
        private Long comments = 0L;
        private Long likes = 0L;
        private Long subscriptions = 0L;
    }
}
