package com.nexora.synergy.platform.domain.repository;

import java.util.Optional;

import com.nexora.synergy.platform.domain.model.DomainName;
import com.nexora.synergy.platform.domain.model.Tenant;
import com.nexora.synergy.platform.domain.valueObject.TenantId;

public interface TenantRepository {

    boolean existsByDomain(DomainName domain);

    void save(Tenant tenant);

    Optional<Tenant> findById(TenantId id);
}
