package com.nexora.synergy.exception;

public class TenantDomainAlreadyExistsException extends DomainException {

    public TenantDomainAlreadyExistsException(String message) {
        super("TENANT_DOMAIN_TAKEN", message);
    }
}
