package com.nexora.synergy.platform.domain.model;

public record DomainName(String value) {

    public DomainName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Domain cannot be empty");
        }
    }
}
