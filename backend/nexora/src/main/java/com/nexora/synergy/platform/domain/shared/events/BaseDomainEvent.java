package com.nexora.synergy.platform.domain.shared.events;

import java.time.Instant;
import java.util.UUID;

/**
 * BaseDomainEvent — concrete base for all domain events.
 *
 * All events carry: eventId — UUID v4, used for outbox deduplication
 * aggregateId — The ID of the aggregate that raised this event correlationId —
 * Propagated from the originating HTTP X-Correlation-ID version — Schema
 * version (start at 1, increment on breaking changes) occurredAt — Event
 * timestamp (UTC)
 */
public abstract class BaseDomainEvent implements DomainEvent {

    private final String eventId;
    private final String aggregateId;
    private final String correlationId;
    private final int version;
    private final Instant occurredAt;

    protected BaseDomainEvent(String aggregateId, String correlationId, int version) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.correlationId = correlationId != null ? correlationId : "no-correlation";
        this.version = version;
        this.occurredAt = Instant.now();
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public String getAggregateId() {
        return aggregateId;
    }

    @Override
    public String getCorrelationId() {
        return correlationId;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }
}
