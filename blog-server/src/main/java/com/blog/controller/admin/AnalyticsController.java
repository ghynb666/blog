package com.blog.controller.admin;

import com.blog.common.Result;
import com.blog.service.GrowthEventService;
import com.blog.vo.AnalyticsOverviewVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/admin/analytics", "/api/v1/admin/analytics"})
public class AnalyticsController {

    private final GrowthEventService growthEventService;

    public AnalyticsController(GrowthEventService growthEventService) {
        this.growthEventService = growthEventService;
    }

    @GetMapping("/overview")
    public Result<AnalyticsOverviewVO> overview(@RequestParam(defaultValue = "7") Integer days) {
        return Result.success(growthEventService.buildOverview(Math.max(1, days)));
    }
}
