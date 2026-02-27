package com.nexora.synergy.platform.domain.repository;

import java.util.List;
import java.util.Optional;

import com.nexora.synergy.platform.domain.model.Tenant;
import com.nexora.synergy.platform.domain.model.TenantDomain;
import com.nexora.synergy.platform.domain.model.TenantId;
import com.nexora.synergy.platform.domain.model.TenantStatus;

/**
 * DOMAIN REPOSITORY INTERFACE: TenantRepository
 *
 * This interface lives in the DOMAIN layer. The implementation lives in
 * INFRASTRUCTURE.
 *
 * Why this separation? - Domain never depends on JPA/SQL/Hibernate -
 * Infrastructure implements this contract - You can swap Postgres for MongoDB
 * without touching domain - Domain can be tested with in-memory fakes
 *
 * Notice: returns Tenant (domain object), not TenantJpaEntity. The
 * infrastructure layer maps between them.
 */
public interface TenantRepository {

    /**
     * Persist a new or updated Tenant. Also handles the insert/update
     * distinction internally.
     */
    Tenant save(Tenant tenant);

    /**
     * Find by primary identifier.
     */
    Optional<Tenant> findById(TenantId id);

    /**
     * Find by subdomain — needed for request routing. e.g., "harvard" resolves
     * to the correct tenant.
     */
    Optional<Tenant> findByDomain(TenantDomain domain);

    /**
     * Find by subdomain string — convenience for routing middleware.
     */
    Optional<Tenant> findByDomainValue(String subdomain);

    /**
     * Check if a domain is already taken — enforces global uniqueness.
     */
    boolean existsByDomain(TenantDomain domain);

    /**
     * Find all tenants in a given status — used by admin operations.
     */
    List<Tenant> findByStatus(TenantStatus status);

    /**
     * Find all tenants — platform admin only.
     */
    List<Tenant> findAll();
}
