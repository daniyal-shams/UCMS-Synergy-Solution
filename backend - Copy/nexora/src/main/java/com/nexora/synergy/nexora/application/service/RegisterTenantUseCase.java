package com.nexora.synergy.nexora.application.service;

import com.nexora.synergy.nexora.application.command.RegisterTenantCommand;
import com.nexora.synergy.nexora.domain.model.PlanType;
import com.nexora.synergy.nexora.domain.model.Tenant;
import com.nexora.synergy.nexora.domain.repository.TenantRepository;
import com.nexora.synergy.nexora.domain.valueObject.DomainName;
import com.nexora.synergy.nexora.domain.valueObject.TenantId;

public class RegisterTenantUseCase {

    private final TenantRepository tenantRepository;

    public RegisterTenantUseCase(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public TenantId execute(RegisterTenantCommand command) {

        DomainName domain = new DomainName(command.domain());
        PlanType plan = PlanType.from(command.planType());

        if (tenantRepository.existsByDomain(domain.toString())) {
            throw new IllegalStateException("Domain already exists");
        }

        Tenant tenant = Tenant.register(
                TenantId.newId(),
                domain.toString(),
                domain.toString(),
                PlanType.INACTIVE,
                false
        );

        tenantRepository.save(tenant);

        /*  
            Static Factory method - to control method creation that enforce invariants at creation time
         */
        return TenantId.newId();

    }

}
