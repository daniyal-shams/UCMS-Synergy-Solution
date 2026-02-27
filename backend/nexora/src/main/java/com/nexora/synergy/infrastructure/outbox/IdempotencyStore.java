package com.nexora.synergy.infrastructure.outbox;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * IdempotencyStore — prevents duplicate API calls from creating duplicate
 * tenants.
 *
 * AWS Equivalent: API Gateway idempotency keys (x-amzn-idempotency-key).
 *
 * Client sends X-Idempotency-Key header on POST /tenants/register. If the same
 * key is seen again within TTL: - If COMPLETE: return the cached result - If
 * IN_PROGRESS: return 409 Conflict - If EXPIRED: treat as new request
 *
 * This prevents the "button clicked twice" problem on flaky networks.
 */
@Entity
@Table(name = "idempotency_records", schema = "platform",
        indexes = @Index(name = "idx_idempotency_key", columnList = "idempotency_key", unique = true))
public class IdempotencyStore {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 255)
    private String idempotencyKey;

    @Column(name = "operation", nullable = false, length = 100)
    private String operation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private IdempotencyStatus status;

    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    protected IdempotencyStore() {
    }

    public static IdempotencyStore createInProgress(String idempotencyKey, String operation, long ttlMinutes) {
        IdempotencyStore rec = new IdempotencyStore();
        rec.id = UUID.randomUUID();
        rec.idempotencyKey = idempotencyKey;
        rec.operation = operation;
        rec.status = IdempotencyStatus.IN_PROGRESS;
        rec.createdAt = Instant.now();
        rec.expiresAt = Instant.now().plusSeconds(ttlMinutes * 60);
        return rec;
    }

    public void complete(String responsePayload) {
        this.status = IdempotencyStatus.COMPLETE;
        this.responsePayload = responsePayload;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isInProgress() {
        return status == IdempotencyStatus.IN_PROGRESS;
    }

    public boolean isComplete() {
        return status == IdempotencyStatus.COMPLETE;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public IdempotencyStatus getStatus() {
        return status;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public String getOperation() {
        return operation;
    }

    public enum IdempotencyStatus {
        IN_PROGRESS, COMPLETE
    }
}
