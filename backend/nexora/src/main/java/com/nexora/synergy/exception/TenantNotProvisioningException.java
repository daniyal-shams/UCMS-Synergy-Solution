package com.nexora.synergy.exception;

public class TenantNotProvisioningException extends DomainException {

    public TenantNotProvisioningException(String message) {
        super("TENANT_NOT_PROVISIONING", message);
    }
}
