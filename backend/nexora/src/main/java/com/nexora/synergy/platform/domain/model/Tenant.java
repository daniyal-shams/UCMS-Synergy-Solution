package com.nexora.synergy.platform.domain.model;

import java.time.Instant;
import java.util.Objects;

import com.nexora.synergy.exception.TenantNotProvisioningException;
import com.nexora.synergy.platform.domain.events.TenantActivatedEvent;
import com.nexora.synergy.platform.domain.events.TenantProvisioningFailedEvent;
import com.nexora.synergy.platform.domain.events.TenantRegisteredEvent;
import com.nexora.synergy.platform.domain.events.TenantSuspendedEvent;

/**
 * AGGREGATE ROOT: Tenant
 *
 * === AWS First-Principles Applied ===
 *
 * 1. SINGLE SOURCE OF TRUTH Status transitions are the ONLY way state changes.
 * No setStatus() method exists. Every change goes through a named behavior.
 *
 * 2. EVENT-DRIVEN STATE MACHINE Every state change produces a domain event.
 * Events carry correlationId for end-to-end distributed tracing.
 *
 * 3. IDEMPOTENT OPERATIONS activate() is idempotent — calling on an
 * already-active tenant is safe. Supports at-least-once delivery guarantees
 * from the Outbox.
 *
 * 4. IMMUTABLE IDENTITY TenantId, TenantDomain, registeredAt never change after
 * construction. These are facts, not mutable fields.
 *
 * 5. SELF-CONTAINED EVENTS Events carry all data needed for downstream
 * processing — no secondary lookups required (SQS/SNS pattern).
 */
public class Tenant extends AggregateRoot {

    // ── Identity (immutable after creation) ──
    private final TenantId id;
    private final TenantDomain domain;
    private final Instant registeredAt;

    // ── Profile ──
    private final String institutionName;
    private final ContactInfo contactInfo;

    // ── Mutable lifecycle state ──
    private TenantStatus status;
    private Instant activatedAt;
    private String schemaName;

    // ── Correlation (for tracing across async operations) ──
    private String correlationId;

    private Tenant(
            TenantId id,
            TenantDomain domain,
            String institutionName,
            ContactInfo contactInfo,
            TenantStatus status,
            Instant registeredAt,
            String correlationId
    ) {
        this.id = Objects.requireNonNull(id);
        this.domain = Objects.requireNonNull(domain);
        this.institutionName = validateInstitutionName(institutionName);
        this.contactInfo = Objects.requireNonNull(contactInfo);
        this.status = Objects.requireNonNull(status);
        this.registeredAt = Objects.requireNonNull(registeredAt);
        this.correlationId = correlationId != null ? correlationId : "no-correlation";
    }

    // =========================================================================
    //  FACTORY — New registration
    // =========================================================================
    static Tenant register(
            TenantId id,
            TenantDomain domain,
            String institutionName,
            ContactInfo contactInfo,
            String correlationId
    ) {
        Tenant tenant = new Tenant(
                id, domain, institutionName, contactInfo,
                TenantStatus.PENDING, Instant.now(), correlationId
        );

        tenant.registerEvent(new TenantRegisteredEvent(
                id.toString(),
                domain.getValue(),
                institutionName,
                contactInfo.getAdminEmail(),
                contactInfo.getAdminName(),
                correlationId
        ));

        return tenant;
    }

    /**
     * Reconstitute from persistence — no events raised. Used by
     * TenantRepositoryImpl only.
     */
    public static Tenant reconstitute(
            TenantId id,
            TenantDomain domain,
            String institutionName,
            ContactInfo contactInfo,
            TenantStatus status,
            Instant registeredAt,
            Instant activatedAt,
            String schemaName,
            String correlationId
    ) {
        Tenant tenant = new Tenant(
                id, domain, institutionName, contactInfo,
                status, registeredAt, correlationId
        );
        tenant.activatedAt = activatedAt;
        tenant.schemaName = schemaName;
        return tenant;
    }

    // =========================================================================
    //  BEHAVIORS — all state mutations go through named methods
    // =========================================================================
    /**
     * PENDING → PROVISIONING
     */
    public void startProvisioning() {
        this.status = this.status.transitionTo(TenantStatus.PROVISIONING);
        this.schemaName = buildSchemaName();
        registerEvent(new TenantProvisioningStartedEvent(id.toString(), schemaName, correlationId));
    }

    /**
     * PROVISIONING → ACTIVE Idempotent: calling on an already-ACTIVE tenant is
     * a no-op. This supports at-least-once processing from the Outbox.
     */
    public void activate() {
        if (this.status == TenantStatus.ACTIVE) {
            return; // Idempotent — already active, nothing to do
        }
        if (this.status != TenantStatus.PROVISIONING) {
            throw new TenantNotProvisioningException(
                    "Tenant " + id + " must be PROVISIONING to activate, was: " + status);
        }
        this.status = this.status.transitionTo(TenantStatus.ACTIVE);
        this.activatedAt = Instant.now();
        registerEvent(new TenantActivatedEvent(
                id.toString(), domain.getValue(), schemaName, contactInfo.getAdminEmail(), correlationId));
    }

    /**
     * ACTIVE → SUSPENDED
     */
    public void suspend(String reason) {
        this.status = this.status.transitionTo(TenantStatus.SUSPENDED);
        registerEvent(new TenantSuspendedEvent(id.toString(), domain.getValue(), reason, correlationId));
    }

    /**
     * SUSPENDED → ACTIVE
     */
    public void reactivate() {
        this.status = this.status.transitionTo(TenantStatus.ACTIVE);
        this.activatedAt = Instant.now();
        registerEvent(new TenantActivatedEvent(
                id.toString(), domain.getValue(), schemaName, contactInfo.getAdminEmail(), correlationId));
    }

    /**
     * PROVISIONING → FAILED
     */
    public void markProvisioningFailed(String reason) {
        this.status = this.status.transitionTo(TenantStatus.FAILED);
        registerEvent(new TenantProvisioningFailedEvent(id.toString(), reason, correlationId));
    }

    /**
     * FAILED → PENDING (allows retry)
     */
    public void resetForRetry(String newCorrelationId) {
        this.status = this.status.transitionTo(TenantStatus.PENDING);
        this.correlationId = newCorrelationId;
        registerEvent(new TenantRegisteredEvent(
                id.toString(), domain.getValue(), institutionName,
                contactInfo.getAdminEmail(), contactInfo.getAdminName(), newCorrelationId));
    }

    // =========================================================================
    //  DOMAIN QUERIES
    // =========================================================================
    public boolean canAcceptRequests() {
        return status == TenantStatus.ACTIVE;
    }

    public boolean isOperational() {
        return status.isOperational();
    }

    public boolean isPending() {
        return status == TenantStatus.PENDING;
    }

    public boolean hasFailed() {
        return status == TenantStatus.FAILED;
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================
    private String buildSchemaName() {
        return "tenant_" + domain.getValue().replace("-", "_");
    }

    private String validateInstitutionName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Institution name must not be blank");
        }
        if (name.length() > 200) {
            throw new IllegalArgumentException("Institution name too long (max 200 chars)");
        }
        return name.trim();
    }

    // =========================================================================
    //  ACCESSORS
    // =========================================================================
    public TenantId getId() {
        return id;
    }

    public TenantDomain getDomain() {
        return domain;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public Instant getActivatedAt() {
        return activatedAt;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    @Override
    public String toString() {
        return "Tenant{id=" + id + ", domain=" + domain + ", status=" + status + "}";
    }
}
