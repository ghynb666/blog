package com.blog.event;

public class ArticleViewedEvent extends BaseDomainEvent<ArticleViewedEvent.Payload> {

    public ArticleViewedEvent(Long operatorId, Long articleId, String ip, String userAgent) {
        super(operatorId, new Payload(articleId, ip, userAgent));
    }

    public static class Payload {
        private final Long articleId;
        private final String ip;
        private final String userAgent;

        public Payload(Long articleId, String ip, String userAgent) {
            this.articleId = articleId;
            this.ip = ip;
            this.userAgent = userAgent;
        }

        public Long getArticleId() {
            return articleId;
        }

        public String getIp() {
            return ip;
        }

        public String getUserAgent() {
            return userAgent;
        }
    }
}
