package com.blog.controller.front;

import com.blog.common.Result;
import com.blog.event.publisher.ArticleDomainEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping({"/api/visit", "/api/v1/visit"})
public class VisitController {

    @Autowired
    private ArticleDomainEventPublisher articleDomainEventPublisher;

    @PostMapping("/{articleId}")
    public Result<Void> recordVisit(@PathVariable Long articleId, HttpServletRequest request) {
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        articleDomainEventPublisher.publishArticleViewed(articleId, ip, userAgent);
        return Result.success();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
