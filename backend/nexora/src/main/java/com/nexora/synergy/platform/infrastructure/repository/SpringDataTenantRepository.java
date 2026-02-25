package com.nexora.synergy.platform.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexora.synergy.platform.infrastructure.jpa.TenantJpaEntity;

import java.util.UUID;

public interface SpringDataTenantRepository
        extends JpaRepository<TenantJpaEntity, UUID> {

    boolean existsByDomain(String domain);
}
