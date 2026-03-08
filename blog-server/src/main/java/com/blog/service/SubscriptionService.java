package com.blog.service;

import com.blog.dto.SubscriptionCreateDTO;
import com.blog.entity.Subscription;

public interface SubscriptionService {

    Subscription create(SubscriptionCreateDTO dto);
}
