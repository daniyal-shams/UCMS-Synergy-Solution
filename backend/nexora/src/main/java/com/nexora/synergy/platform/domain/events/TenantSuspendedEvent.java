package com.nexora.synergy.platform.domain.events;

import com.nexora.synergy.platform.domain.shared.events.BaseDomainEvent;

public final class TenantSuspendedEvent extends BaseDomainEvent {

    private final String subdomain;
    private final String reason;

    public TenantSuspendedEvent(String tenantId, String subdomain, String reason, String correlationId) {
        super(tenantId, correlationId, 1);
        this.subdomain = subdomain;
        this.reason = reason;
    }

    @Override
    public String getEventType() {
        return "tenant.suspended.v1";
    }

    public String getSubdomain() {
        return subdomain;
    }

    public String getReason() {
        return reason;
    }
}
