package com.nexora.synergy.platform.domain.events;

import com.nexora.synergy.platform.domain.shared.events.BaseDomainEvent;

/**
 * TenantProvisioningFailedEvent v1 Consumed by: NotificationService (admin
 * alert), AuditService
 */
public final class TenantProvisioningFailedEvent extends BaseDomainEvent {

    private final String reason;

    public TenantProvisioningFailedEvent(String tenantId, String reason, String correlationId) {
        super(tenantId, correlationId, 1);
        this.reason = reason;
    }

    @Override
    public String getEventType() {
        return "tenant.provisioning.failed.v1";
    }

    public String getReason() {
        return reason;
    }
}
