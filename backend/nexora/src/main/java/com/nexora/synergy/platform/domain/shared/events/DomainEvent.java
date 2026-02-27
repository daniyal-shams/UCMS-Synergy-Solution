package com.nexora.synergy.platform.domain.shared.events;

import java.time.Instant;

/**
 * DomainEvent — immutable fact that something happened in the domain.
 *
 * AWS Standard: Events must carry enough data to be processed independently
 * (self-contained, no lookups needed for basic handling).
 *
 * All events are: - Immutable (records) - Self-identifying (eventId, eventType,
 * version) - Traceable (correlationId propagated from the originating request)
 * - Idempotent-safe (eventId used for deduplication in Outbox)
 */
public interface DomainEvent {

    String getEventId();

    String getEventType();

    String getAggregateId();

    String getCorrelationId();  // Propagated X-Correlation-ID from HTTP request

    int getVersion();        // Event schema version for forward compatibility

    Instant getOccurredAt();
}
