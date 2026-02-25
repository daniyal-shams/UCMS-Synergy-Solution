package com.nexora.synergy.platform.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexora.synergy.platform.domain.model.DomainName;
import com.nexora.synergy.platform.domain.model.PlanType;
import com.nexora.synergy.platform.domain.model.Tenant;
import com.nexora.synergy.platform.domain.repository.TenantRepository;
import com.nexora.synergy.platform.domain.valueObject.TenantId;

@Service
public class RegisterTenantUseCase {

    private final TenantRepository tenantRepository;
    private final OutboxService outboxService;
    private final IdempotencyService idempotencyService;

    public RegisterTenantUseCase(
            TenantRepository tenantRepository,
            OutboxService outboxService,
            IdempotencyService idempotencyService
    ) {
        this.tenantRepository = tenantRepository;
        this.outboxService = outboxService;
        this.idempotencyService = idempotencyService;
    }

    @Transactional
    public TenantId execute(RegisterTenantCommand command, String idempotencyKey) {

        if (idempotencyService.exists(idempotencyKey)) {
            return idempotencyService.getResult(idempotencyKey);
        }

        DomainName domain = new DomainName(command.domain());

        if (tenantRepository.existsByDomain(domain)) {
            throw new IllegalStateException("Domain already exists");
        }

        Tenant tenant = Tenant.register(
                domain,
                new OrganizationName(command.organizationName()),
                PlanType.valueOf(command.planType())
        );

        tenantRepository.save(tenant);

        outboxService.saveEvent(
                "TenantRegisteredEvent",
                tenant.getTenantId().value().toString()
        );

        idempotencyService.store(idempotencyKey, tenant.getTenantId());

        return tenant.getTenantId();
    }
}
