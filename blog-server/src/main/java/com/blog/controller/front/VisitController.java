package com.blog.controller.front;

import com.blog.common.Result;
import com.blog.service.ArticleService;
import com.blog.service.VisitLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/visit")
public class VisitController {

    @Autowired
    private VisitLogService visitLogService;

    @Autowired
    private ArticleService articleService;

    @PostMapping("/{articleId}")
    public Result<Void> recordVisit(@PathVariable Long articleId, HttpServletRequest request) {
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        visitLogService.recordVisit(articleId, ip, userAgent);

        if (visitLogService.canCount(articleId, ip)) {
            articleService.incrementViewCount(articleId);
        }

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
