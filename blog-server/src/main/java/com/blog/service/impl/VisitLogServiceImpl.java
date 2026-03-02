package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.VisitLog;
import com.blog.mapper.VisitLogMapper;
import com.blog.service.VisitLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VisitLogServiceImpl extends ServiceImpl<VisitLogMapper, VisitLog> implements VisitLogService {

    @Override
    public void recordVisit(Long articleId, String ip, String userAgent) {
        VisitLog log = new VisitLog();
        log.setArticleId(articleId);
        log.setIp(ip);
        log.setUserAgent(userAgent);
        log.setCreatedAt(LocalDateTime.now());
        save(log);
    }

    @Override
    public boolean canCount(Long articleId, String ip) {
        LocalDateTime yesterday = LocalDateTime.now().minusHours(24);
        long count = count(new LambdaQueryWrapper<VisitLog>()
                .eq(VisitLog::getArticleId, articleId)
                .eq(VisitLog::getIp, ip)
                .ge(VisitLog::getCreatedAt, yesterday));
        return count == 0;
    }
}
