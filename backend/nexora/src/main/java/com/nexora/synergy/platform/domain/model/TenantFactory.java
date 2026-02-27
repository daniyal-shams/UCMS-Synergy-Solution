package com.nexora.synergy.platform.domain.model;

import org.springframework.stereotype.Component;

/**
 * TenantFactory — creates new Tenant aggregates.
 *
 * Now accepts correlationId so the originating request's trace ID propagates
 * through all downstream events and async operations. This mirrors how AWS
 * X-Ray propagates trace context.
 */
@Component
public class TenantFactory {

    public Tenant createNew(
            String subdomain,
            String institutionName,
            String adminName,
            String adminEmail,
            String adminPhone,
            String correlationId // ← NEW: X-Correlation-ID from HTTP request
    ) {
        TenantId id = TenantId.generate();
        TenantDomain domain = TenantDomain.of(subdomain);
        ContactInfo contactInfo = ContactInfo.of(adminName, adminEmail, adminPhone);

        return Tenant.register(id, domain, institutionName, contactInfo, correlationId);
    }
}
