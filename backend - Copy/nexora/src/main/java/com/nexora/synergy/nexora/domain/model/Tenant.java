package com.nexora.synergy.nexora.domain.model;

import java.util.Objects;

import com.nexora.synergy.nexora.domain.valueObject.TenantId;

/**
 * Aggregate Root — Tenant
 *
 * Represents an institution tenant in the platform.
 *
 * Enforces: - domain uniqueness - lifecycle transitions - activation rules -
 * provisioning state
 *
 * DDD Rules: - No framework annotations - Pure domain logic - No infrastructure
 * dependencies
 */
public class Tenant {

    private TenantId id;
    private String organizationName;
    private String domain;
    private PlanType planType;
    private TenantStatus status;

    // Required for ORM / reconstruction
    protected Tenant() {
    }

    private Tenant(
            TenantId id,
            String organizationName,
            String domain,
            PlanType planType,
            TenantStatus status
    ) {
        this.id = Objects.requireNonNull(id);
        this.organizationName = validateOrgName(organizationName);
        this.domain = validateDomain(domain);
        this.planType = Objects.requireNonNull(planType);
        this.status = Objects.requireNonNull(status);
    }

    // =====================================================
    // FACTORY METHOD — Tenant Registration
    // =====================================================
    /**
     * Registers a new tenant.
     *
     * Business Rules: - Domain must be unique (checked externally) - Initial
     * state must be PENDING
     */
    public static Tenant register(
            TenantId id,
            String organizationName,
            String domain,
            PlanType planType,
            boolean domainAlreadyExists
    ) {

        if (domainAlreadyExists) {
            throw new IllegalStateException("Domain already taken: " + domain);
        }

        return new Tenant(
                id,
                organizationName,
                domain,
                planType,
                TenantStatus.PENDING
        );
    }

    // =====================================================
    // DOMAIN BEHAVIOR — Lifecycle Management
    // =====================================================
    /**
     * Activate tenant after successful provisioning.
     *
     * Rule: Only PENDING tenant can become ACTIVE.
     */
    public void activate() {
        if (status != TenantStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending tenant can be activated. Current: " + status
            );
        }
        this.status = TenantStatus.ACTIVE;
    }

    /**
     * Suspend tenant (billing expiry, policy violation).
     */
    public void suspend() {
        if (status != TenantStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Only active tenant can be suspended"
            );
        }
        this.status = TenantStatus.SUSPENDED;
    }

    /**
     * Mark provisioning failure.
     */
    public void failProvisioning() {
        if (status != TenantStatus.PENDING) {
            throw new IllegalStateException(
                    "Provisioning can only fail from PENDING state"
            );
        }
        this.status = TenantStatus.FAILED;
    }

    // =====================================================
    // VALIDATION RULES (Domain Invariants)
    // =====================================================
    private String validateOrgName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Organization name required");
        }
        return name.trim();
    }

    private String validateDomain(String domain) {
        if (domain == null || domain.isBlank()) {
            throw new IllegalArgumentException("Domain required");
        }

        if (!domain.matches("^[a-z0-9-]+$")) {
            throw new IllegalArgumentException(
                    "Domain must be lowercase alphanumeric with hyphens"
            );
        }

        return domain.toLowerCase();
    }

    // =====================================================
    // GETTERS
    // =====================================================
    public TenantId getId() {
        return id;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getDomain() {
        return domain;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public TenantStatus getStatus() {
        return status;
    }

    // =====================================================
    // EQUALITY (Aggregate identity by TenantId)
    // =====================================================
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tenant)) {
            return false;
        }
        Tenant tenant = (Tenant) o;
        return Objects.equals(id, tenant.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

// What This Aggregate Enforces
// 1️⃣ Domain Invariants
// - organization name required
// - domain format validation
// - domain uniqueness (checked externally)
// 2️⃣ Lifecycle Rules
// register() → PENDING
// PENDING → ACTIVE
// PENDING → FAILED
// ACTIVE → SUSPENDED
// Illegal transitions throw exceptions.
// 3️⃣ Behavior-Driven Model
// Instead of:
// tenant.setStatus("ACTIVE") ❌
// We use:
// tenant.activate() ✅
// tenant.suspend() ✅
// tenant.failProvisioning() ✅
// This is real DDD.
// 4️⃣ Aggregate Boundary
// Tenant is the consistency boundary.
// All state changes go through this object.
// No external mutation.

