package com.nexora.synergy.platform.domain.events;

import com.nexora.synergy.platform.domain.shared.events.BaseDomainEvent;

/**
 * TenantActivatedEvent v1 Consumed by: NotificationService (welcome email),
 * BillingService (start trial)
 */
public final class TenantActivatedEvent extends BaseDomainEvent {

    private final String subdomain;
    private final String schemaName;
    private final String adminEmail;

    public TenantActivatedEvent(
            String tenantId,
            String subdomain,
            String schemaName,
            String adminEmail,
            String correlationId
    ) {
        super(tenantId, correlationId, 1);
        this.subdomain = subdomain;
        this.schemaName = schemaName;
        this.adminEmail = adminEmail;
    }

    @Override
    public String getEventType() {
        return "tenant.activated.v1";
    }

    public String getSubdomain() {
        return subdomain;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }
}
