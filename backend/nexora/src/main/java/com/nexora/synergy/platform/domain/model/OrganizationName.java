package com.nexora.synergy.platform.domain.model;

public record OrganizationName(String value) {

    public OrganizationName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Organization name cannot be empty");
        }
    }
}
