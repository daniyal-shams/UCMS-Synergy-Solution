package com.nexora.synergy.billing.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexora.synergy.billing.application.command.ActivateSubscriptionCommand;
import com.nexora.synergy.billing.domain.Subscription;
import com.nexora.synergy.platform.domain.model.Tenant;
import com.nexora.synergy.platform.domain.repository.TenantRepository;
import com.nexora.synergy.platform.domain.valueObject.TenantId;
import com.nexora.synergy.shared.outbox.service.OutboxService;

import java.util.UUID;

@Service
public class ActivateSubscriptionUseCase {

    private final TenantRepository tenantRepository;
    private final OutboxService outboxService;

    public ActivateSubscriptionUseCase(
            TenantRepository tenantRepository,
            OutboxService outboxService
    ) {
        this.tenantRepository = tenantRepository;
        this.outboxService = outboxService;
    }

    @Transactional
    public void execute(ActivateSubscriptionCommand command) {

        Tenant tenant = tenantRepository.findById(
                new TenantId(command.tenantId())
        ).orElseThrow(()
                -> new IllegalStateException("Tenant not found")
        );

        Subscription subscription
                = Subscription.activate(command.billingCycle());

        if (!subscription.isActive()) {
            throw new IllegalStateException("Subscription activation failed");
        }

        tenant.activateSubscription();

        tenantRepository.save(tenant);

        outboxService.saveEvent(
                "SubscriptionActivatedEvent",
                "{\"tenantId\":\"" + command.tenantId() + "\"}"
        );
    }
}
