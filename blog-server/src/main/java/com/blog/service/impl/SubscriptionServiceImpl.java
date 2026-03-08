package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.AppException;
import com.blog.common.ErrorCode;
import com.blog.dto.SubscriptionCreateDTO;
import com.blog.entity.Subscription;
import com.blog.mapper.SubscriptionMapper;
import com.blog.service.GrowthEventService;
import com.blog.service.SubscriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionMapper subscriptionMapper;
    private final GrowthEventService growthEventService;

    public SubscriptionServiceImpl(SubscriptionMapper subscriptionMapper, GrowthEventService growthEventService) {
        this.subscriptionMapper = subscriptionMapper;
        this.growthEventService = growthEventService;
    }

    @Override
    @Transactional
    public Subscription create(SubscriptionCreateDTO dto) {
        Subscription existed = subscriptionMapper.selectOne(new LambdaQueryWrapper<Subscription>()
                .eq(Subscription::getEmail, dto.getEmail())
                .last("limit 1"));
        if (existed != null) {
            throw new AppException(ErrorCode.SUBSCRIPTION_ALREADY_EXISTS);
        }
        Subscription subscription = new Subscription();
        subscription.setEmail(dto.getEmail());
        subscription.setSourcePage(dto.getSourcePage());
        subscription.setStatus("ACTIVE");
        subscriptionMapper.insert(subscription);
        growthEventService.record(
                "subscription_created",
                null,
                null,
                "{\"sourcePage\":\"" + safeValue(dto.getSourcePage()) + "\"}"
        );
        return subscription;
    }

    private String safeValue(String sourcePage) {
        if (sourcePage == null) {
            return "";
        }
        return sourcePage.replace("\"", "'");
    }
}
