package com.nexora.synergy.platform.domain.events;

import com.nexora.synergy.platform.domain.shared.events.BaseDomainEvent;

/**
 * TenantRegisteredEvent v1
 *
 * Emitted when a Tenant aggregate is created (status = PENDING). Consumed by:
 * TenantProvisioningService (async, via Outbox)
 *
 * Self-contained: carries all data provisioning needs — no secondary DB lookup
 * required to begin provisioning.
 */
public final class TenantRegisteredEvent extends BaseDomainEvent {

    private final String subdomain;
    private final String institutionName;
    private final String adminEmail;
    private final String adminName;

    public TenantRegisteredEvent(
            String tenantId,
            String subdomain,
            String institutionName,
            String adminEmail,
            String adminName,
            String correlationId
    ) {
        super(tenantId, correlationId, 1);
        this.subdomain = subdomain;
        this.institutionName = institutionName;
        this.adminEmail = adminEmail;
        this.adminName = adminName;
    }

    @Override
    public String getEventType() {
        return "tenant.registered.v1";
    }

    public String getSubdomain() {
        return subdomain;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public String getAdminName() {
        return adminName;
    }
}
