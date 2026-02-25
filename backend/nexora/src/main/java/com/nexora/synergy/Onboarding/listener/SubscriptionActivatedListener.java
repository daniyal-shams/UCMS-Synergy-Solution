package com.nexora.synergy.Onboarding.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.nexora.synergy.provisioning.TenantProvisioningService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SubscriptionActivatedListener {

    private final TenantProvisioningService provisioningService;

    public SubscriptionActivatedListener(
            TenantProvisioningService provisioningService
    ) {
        this.provisioningService = provisioningService;
    }

    @RabbitListener(queues = "platform.subscription.queue")
    public void handleSubscriptionActivated(String payload) {

        String tenantId = payload.replaceAll("[^a-fA-F0-9-]", "");

        provisioningService.provisionTenantDatabase(
                UUID.fromString(tenantId)
        );

        provisioningService.createPrimaryCampusSchema(
                UUID.fromString(tenantId),
                "campus_main"
        );
    }
}
