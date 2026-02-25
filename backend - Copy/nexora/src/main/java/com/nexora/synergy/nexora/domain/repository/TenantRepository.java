package com.nexora.synergy.nexora.domain.repository;

import java.util.Optional;

import com.nexora.synergy.nexora.domain.model.Tenant;
import com.nexora.synergy.nexora.domain.valueObject.TenantId;

public interface TenantRepository {

    /**
     * Check if a tenant domain already exists. Used to enforce domain
     * uniqueness invariant.
     */
    boolean existsByDomain(String domain);

    /**
     * Save or update tenant aggregate.
     */
    void save(Tenant tenant);

    /**
     * Find tenant by identity.
     */
    Optional<Tenant> findById(TenantId id);

}
