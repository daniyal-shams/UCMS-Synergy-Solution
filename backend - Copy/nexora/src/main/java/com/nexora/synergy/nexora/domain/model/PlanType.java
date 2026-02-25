package com.nexora.synergy.nexora.domain.model;

/**
 * Subscription plan type.
 */
public enum PlanType {

    INACTIVE,
    BASIC,
    PRO,
    ENTERPRISE;

    public static PlanType from(String value) {
        try {
            return PlanType.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid plan type: " + value);
        }
    }
}
