package com.nexora.synergy.platform.domain.model;

import java.time.Instant;
import java.util.UUID;

import com.nexora.synergy.platform.domain.valueObject.TenantId;

import java.time.Instant;

public class Tenant {

    private final TenantId tenantId;
    private final DomainName domain;
    private final OrganizationName organizationName;
    private final PlanType planType;
    private TenantStatus status;
    private final Instant createdAt;

    private Tenant(
            TenantId tenantId,
            DomainName domain,
            OrganizationName organizationName,
            PlanType planType
    ) {
        this.tenantId = tenantId;
        this.domain = domain;
        this.organizationName = organizationName;
        this.planType = planType;
        this.status = TenantStatus.PENDING_VERIFICATION;
        this.createdAt = Instant.now();
    }

    public static Tenant register(
            DomainName domain,
            OrganizationName organizationName,
            PlanType planType
    ) {
        return new Tenant(
                TenantId.newId(),
                domain,
                organizationName,
                planType
        );
    }

    public void activateSubscription() {
        if (this.status != TenantStatus.VERIFIED) {
            throw new IllegalStateException("Tenant must be verified first");
        }
        this.status = TenantStatus.SUBSCRIPTION_ACTIVE;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public DomainName getDomain() {
        return domain;
    }

    public OrganizationName getOrganizationName() {
        return organizationName;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
