package com.nexora.synergy.platform.application.tenant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.nexora.synergy.exception.TenantNotFoundException;
import com.nexora.synergy.platform.domain.model.Tenant;
import com.nexora.synergy.platform.domain.model.TenantId;
import com.nexora.synergy.platform.domain.model.TenantStatus;
import com.nexora.synergy.platform.domain.repository.TenantRepository;

/**
 * APPLICATION SERVICE: TenantQueryService
 *
 * CQRS Separation: Commands (writes) and Queries (reads) in separate services.
 *
 * Why separate? - Reads don't need write transaction overhead - Query
 * complexity grows independently from command complexity - Cleaner API —
 * command services only have execute(), query services only have find*() -
 * Easier to add read-replicas or caching later
 *
 * Read-only transaction — signals to Spring/DB that no writes will happen.
 */
@Service
@Transactional(readOnly = true)
public class TenantQueryService {

    private final TenantRepository tenantRepository;

    public TenantQueryService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Tenant findById(String tenantId) {
        return tenantRepository
                .findById(TenantId.of(tenantId))
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found: " + tenantId));
    }

    public Tenant findBySubdomain(String subdomain) {
        return tenantRepository
                .findByDomainValue(subdomain)
                .orElseThrow(() -> new TenantNotFoundException("No tenant for subdomain: " + subdomain));
    }

    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    public List<Tenant> findByStatus(TenantStatus status) {
        return tenantRepository.findByStatus(status);
    }

    /**
     * Routing check: Is this subdomain mapped to an active tenant? Used by the
     * TenantResolutionFilter for every incoming request.
     */
    public boolean isActiveTenant(String subdomain) {
        return tenantRepository
                .findByDomainValue(subdomain)
                .map(Tenant::canAcceptRequests)
                .orElse(false);
    }
}
