package com.nexora.synergy.billing.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Subscription {

    private final BillingCycle billingCycle;
    private final Instant startDate;
    private final Instant expiryDate;
    private final SubscriptionStatus status;

    private Subscription(
            BillingCycle billingCycle,
            Instant startDate,
            Instant expiryDate,
            SubscriptionStatus status
    ) {
        this.billingCycle = billingCycle;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public static Subscription activate(BillingCycle cycle) {

        Instant now = Instant.now();
        Instant expiry = cycle == BillingCycle.MONTHLY
                ? now.plus(30, ChronoUnit.DAYS)
                : now.plus(365, ChronoUnit.DAYS);

        return new Subscription(
                cycle,
                now,
                expiry,
                SubscriptionStatus.ACTIVE
        );
    }

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }
}
