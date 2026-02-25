package com.nexora.synergy.platform.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.nexora.synergy.platform.domain.model.DomainName;
import com.nexora.synergy.platform.domain.model.OrganizationName;
import com.nexora.synergy.platform.domain.model.Tenant;
import com.nexora.synergy.platform.domain.repository.TenantRepository;
import com.nexora.synergy.platform.domain.valueObject.TenantId;
import com.nexora.synergy.platform.infrastructure.jpa.TenantJpaEntity;

@Repository
public class TenantRepositoryImpl implements TenantRepository {

    private final SpringDataTenantRepository jpaRepository;

    public TenantRepositoryImpl(SpringDataTenantRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existsByDomain(DomainName domain) {
        return jpaRepository.existsByDomain(domain.value());
    }

    @Override
    public void save(Tenant tenant) {
        TenantJpaEntity entity = new TenantJpaEntity();
        entity.setId(tenant.getTenantId().value());
        entity.setDomain(tenant.getDomain().value());
        entity.setOrganizationName(tenant.getOrganizationName().value());
        entity.setPlanType(tenant.getPlanType());
        entity.setStatus(tenant.getStatus());
        entity.setCreatedAt(tenant.getCreatedAt());

        jpaRepository.save(entity);
    }

    @Override
    public Optional<Tenant> findById(TenantId id) {
        return jpaRepository.findById(id.value())
                .map(entity -> Tenant.register(
                new DomainName(entity.getDomain()),
                new OrganizationName(entity.getOrganizationName()),
                entity.getPlanType()
        ));
    }
}
