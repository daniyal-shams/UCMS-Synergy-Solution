package com.nexora.synergy.platform.domain.model;

import com.nexora.synergy.platform.domain.shared.events.BaseDomainEvent;

public final class TenantProvisioningStartedEvent extends BaseDomainEvent {

    private final String schemaName;

    public TenantProvisioningStartedEvent(String tenantId, String schemaName, String correlationId) {
        super(tenantId, correlationId, 1);
        this.schemaName = schemaName;
    }

    @Override
    public String getEventType() {
        return "tenant.provisioning.started.v1";
    }

    public String getSchemaName() {
        return schemaName;
    }
}
