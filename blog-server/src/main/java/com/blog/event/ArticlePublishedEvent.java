package com.blog.event;

public class ArticlePublishedEvent extends BaseDomainEvent<ArticlePublishedEvent.Payload> {

    public ArticlePublishedEvent(Long operatorId, Long articleId, String title) {
        super(operatorId, new Payload(articleId, title));
    }

    public static class Payload {
        private final Long articleId;
        private final String title;

        public Payload(Long articleId, String title) {
            this.articleId = articleId;
            this.title = title;
        }

        public Long getArticleId() {
            return articleId;
        }

        public String getTitle() {
            return title;
        }
    }
}
