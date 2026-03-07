package com.blog.event.listener;

import com.blog.entity.Article;
import com.blog.event.ArticleViewedEvent;
import com.blog.mapper.ArticleMapper;
import com.blog.service.VisitLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ArticleViewEventListener {

    private final VisitLogService visitLogService;
    private final ArticleMapper articleMapper;

    public ArticleViewEventListener(VisitLogService visitLogService, ArticleMapper articleMapper) {
        this.visitLogService = visitLogService;
        this.articleMapper = articleMapper;
    }

    @Async("domainEventExecutor")
    @TransactionalEventListener(fallbackExecution = true)
    @Transactional
    public void onArticleViewed(ArticleViewedEvent event) {
        Long articleId = event.getPayload().getArticleId();
        String ip = event.getPayload().getIp();

        if (ip == null || ip.isBlank()) {
            return;
        }

        if (visitLogService.canCount(articleId, ip)) {
            Article article = articleMapper.selectById(articleId);
            if (article != null) {
                article.setViewCount(article.getViewCount() + 1);
                articleMapper.updateById(article);
            }
        }

        visitLogService.recordVisit(articleId, ip, event.getPayload().getUserAgent());
    }
}
