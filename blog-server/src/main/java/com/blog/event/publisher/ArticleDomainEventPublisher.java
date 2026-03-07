package com.blog.event.publisher;

import com.blog.event.ArticlePublishedEvent;
import com.blog.event.ArticleUpdatedEvent;
import com.blog.event.ArticleViewedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ArticleDomainEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public ArticleDomainEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishArticlePublished(Long operatorId, Long articleId, String title) {
        eventPublisher.publishEvent(new ArticlePublishedEvent(operatorId, articleId, title));
    }

    public void publishArticleUpdated(Long operatorId, Long articleId, String title) {
        eventPublisher.publishEvent(new ArticleUpdatedEvent(operatorId, articleId, title));
    }

    public void publishArticleViewed(Long articleId, String ip, String userAgent) {
        eventPublisher.publishEvent(new ArticleViewedEvent(null, articleId, ip, userAgent));
    }
}
