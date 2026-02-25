package com.nexora.synergy.nexora.api.dto;

import java.util.UUID;

public record RegisterTenantResponse(
        UUID tenantId,
        String status
        ) {

}
