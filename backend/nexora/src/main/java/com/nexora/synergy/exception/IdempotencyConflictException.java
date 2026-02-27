package com.nexora.synergy.exception;

/**
 * Raised when a request with a known idempotency key arrives
 * but its result is not yet available (concurrent duplicate request).
 */
public class IdempotencyConflictException extends DomainException {
    public IdempotencyConflictException(String idempotencyKey) {
        super("IDEMPOTENCY_CONFLICT",
              "A request with idempotency key '" + idempotencyKey + "' is already being processed");
    }
}
