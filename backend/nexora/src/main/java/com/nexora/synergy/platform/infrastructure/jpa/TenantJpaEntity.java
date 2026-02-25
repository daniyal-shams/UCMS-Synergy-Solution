package com.nexora.synergy.platform.infrastructure.jpa;

import java.time.Instant;
import java.util.UUID;

import com.nexora.synergy.platform.domain.model.PlanType;
import com.nexora.synergy.platform.domain.model.TenantStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tenants", uniqueConstraints = {
    @UniqueConstraint(columnNames = "domain")
})
public class TenantJpaEntity {

    @Id
    private UUID id;

    private String domain;

    private String organizationName;

    @Enumerated(EnumType.STRING)
    private PlanType planType;

    @Enumerated(EnumType.STRING)
    private TenantStatus status;

    private Instant createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public void setStatus(TenantStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
