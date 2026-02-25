package com.nexora.synergy.nexora.domain.valueObject;

import java.util.UUID;

public record TenantId(UUID value) {

    public TenantId {
        if (value == null) {
            throw new IllegalArgumentException("TenantId cannot be null");
        }
    }

    public static TenantId newId() {
        return new TenantId(UUID.randomUUID());
    }

}
