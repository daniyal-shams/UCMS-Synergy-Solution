package com.nexora.synergy.exception;

public class InvalidTenantStateTransitionException extends DomainException {

    public InvalidTenantStateTransitionException(String message) {
        super("INVALID_STATE_TRANSITION", message);
    }
}
