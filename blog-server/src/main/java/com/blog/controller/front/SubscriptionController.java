package com.blog.controller.front;

import com.blog.common.Result;
import com.blog.dto.SubscriptionCreateDTO;
import com.blog.entity.Subscription;
import com.blog.service.SubscriptionService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping({"/api", "/api/v1"})
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/subscriptions")
    public Result<Subscription> create(@Valid @RequestBody SubscriptionCreateDTO dto) {
        return Result.success(subscriptionService.create(dto));
    }
}
