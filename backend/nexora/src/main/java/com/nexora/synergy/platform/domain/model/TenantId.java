package com.nexora.synergy.platform.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object: TenantId
 *
 * Value Objects have no identity — they are equal by value. They are immutable
 * and self-validating.
 *
 * Using a typed ID (not raw UUID/String) prevents: - Passing wrong IDs to wrong
 * methods - Confusion between TenantId, StudentId, etc.
 */
public final class TenantId {

    private final UUID value;

    private TenantId(UUID value) {
        this.value = Objects.requireNonNull(value, "TenantId cannot be null");
    }

    /**
     * Factory: Generate a new unique TenantId.
     */
    public static TenantId generate() {
        return new TenantId(UUID.randomUUID());
    }

    /**
     * Factory: Reconstruct from stored UUID string (for queries).
     */
    public static TenantId of(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("TenantId string must not be blank");
        }
        try {
            return new TenantId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid TenantId format: " + id);
        }
    }

    /**
     * Factory: Reconstruct from UUID directly.
     */
    public static TenantId of(UUID id) {
        return new TenantId(id);
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TenantId other)) {
            return false;
        }
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
