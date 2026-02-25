package com.nexora.synergy.billing.application.command;

import java.util.UUID;

import com.nexora.synergy.billing.domain.BillingCycle;

public record ActivateSubscriptionCommand(
        UUID tenantId,
        BillingCycle billingCycle
        ) {

}
