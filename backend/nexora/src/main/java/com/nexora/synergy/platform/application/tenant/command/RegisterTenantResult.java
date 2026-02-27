package com.nexora.synergy.platform.application.tenant.command;

import com.nexora.synergy.platform.domain.model.TenantStatus;
import java.time.Instant;

/**
 * APPLICATION RESULT: RegisterTenantResult
 *
 * The output returned by the RegisterTenantApplicationService. Contains only
 * the data callers need — not the full domain object.
 *
 * Why not return the Tenant aggregate? - Aggregates are internal to the domain
 * - Results are shaped for the API consumer - Prevents leaking domain internals
 * to the interface layer
 */
public record RegisterTenantResult(
        String tenantId,
        String subdomain,
        String institutionName,
        String fullDomain,
        TenantStatus status,
        Instant registeredAt
        ) {

}
