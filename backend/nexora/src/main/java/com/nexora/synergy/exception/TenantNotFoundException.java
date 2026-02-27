package com.nexora.synergy.exception;

public class TenantNotFoundException extends DomainException {

    public TenantNotFoundException(String message) {
        super("TENANT_NOT_FOUND", message);
    }
}
