package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.VisitLog;

public interface VisitLogService extends IService<VisitLog> {

    void recordVisit(Long articleId, String ip, String userAgent);

    boolean canCount(Long articleId, String ip);
}
