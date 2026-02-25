package com.nexora.synergy.nexora.domain.valueObject;

public record DomainName(String value) {

    public DomainName {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Domain cannot be empty");
        }

        // simple domain validation
        if (!value.matches("^[a-zA-Z0-9-]+$")) {
            throw new IllegalArgumentException("Invalid domain format");
        }
    }

}
