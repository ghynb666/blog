package com.blog.event;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseDomainEvent<T> {

    private final String eventId;
    private final LocalDateTime occurredAt;
    private final Long operatorId;
    private final T payload;

    protected BaseDomainEvent(Long operatorId, T payload) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = LocalDateTime.now();
        this.operatorId = operatorId;
        this.payload = payload;
    }

    public String getEventId() {
        return eventId;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public T getPayload() {
        return payload;
    }
}
