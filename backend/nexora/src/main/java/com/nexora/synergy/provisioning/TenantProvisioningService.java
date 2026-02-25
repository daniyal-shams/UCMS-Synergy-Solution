package com.nexora.synergy.provisioning;

import java.util.UUID;

public interface TenantProvisioningService {

    void provisionTenantDatabase(UUID tenantId);

    void createPrimaryCampusSchema(UUID tenantId, String schemaName);
}
