package com.nexora.synergy.platform.interfaces.rest.tenant;

import java.util.UUID;

/**
 * DTO for the tenant registration response.
 */
public record TenantRegistrationResponse(
        UUID tenantId,
        String status,
        String message
        ) {

}
