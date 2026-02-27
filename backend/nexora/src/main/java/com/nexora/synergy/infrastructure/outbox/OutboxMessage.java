package com.nexora.synergy.infrastructure.outbox;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * OUTBOX PATTERN: OutboxMessage — AWS at-least-once delivery guarantee.
 *
 * Aggregate + OutboxMessage are saved in ONE transaction. OutboxPoller
 * dispatches PENDING messages, marks PROCESSED after success. Failed messages
 * retry with exponential backoff → DEAD_LETTER after maxRetries.
 */
@Entity
@Table(name = "outbox_messages", schema = "platform",
        indexes = {
            @Index(name = "idx_outbox_status_retry", columnList = "status, next_retry_at"),
            @Index(name = "idx_outbox_idempotency", columnList = "idempotency_key", unique = true)
        }
)
public class OutboxMessage {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 36)
    private String aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "idempotency_key", nullable = false, length = 200, updatable = false)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OutboxStatus status;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "next_retry_at")
    private Instant nextRetryAt;

    protected OutboxMessage() {
    }

    public static OutboxMessage create(String aggregateType, String aggregateId,
            String eventType, String payload, String correlationId, String idempotencyKey) {
        OutboxMessage m = new OutboxMessage();
        m.id = UUID.randomUUID();
        m.aggregateType = aggregateType;
        m.aggregateId = aggregateId;
        m.eventType = eventType;
        m.payload = payload;
        m.correlationId = correlationId;
        m.idempotencyKey = idempotencyKey;
        m.status = OutboxStatus.PENDING;
        m.retryCount = 0;
        m.createdAt = Instant.now();
        m.nextRetryAt = Instant.now();
        return m;
    }

    public void markProcessing() {
        this.status = OutboxStatus.PROCESSING;
    }

    public void markProcessed() {
        this.status = OutboxStatus.PROCESSED;
        this.processedAt = Instant.now();
    }

    public void markFailed(String error, int maxRetries) {
        this.retryCount++;
        this.lastError = error;
        this.status = (retryCount >= maxRetries) ? OutboxStatus.DEAD_LETTER : OutboxStatus.PENDING;
        if (status == OutboxStatus.PENDING) {
            this.nextRetryAt = Instant.now().plusSeconds((long) Math.pow(2, retryCount));
        }
    }

    public UUID getId() {
        return id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getLastError() {
        return lastError;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getNextRetryAt() {
        return nextRetryAt;
    }

    public enum OutboxStatus {
        PENDING, PROCESSING, PROCESSED, DEAD_LETTER
    }
}
