package com.nexora.synergy.infrastructure.correlation;

import java.util.UUID;

/**
 * CorrelationContext — thread-local distributed trace ID.
 *
 * AWS Equivalent: X-Ray Trace ID propagated across service boundaries.
 *
 * Every HTTP request gets a unique X-Correlation-ID. This ID propagates
 * through: - All log entries (via MDC) - All domain events (correlationId
 * field) - All outbox messages - All async provisioning operations
 *
 * This means you can grep logs by correlationId and see the complete story of a
 * tenant registration across all layers.
 */
public final class CorrelationContext {

    private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();

    private CorrelationContext() {
    }

    public static void set(String correlationId) {
        CORRELATION_ID.set(correlationId);
    }

    public static String get() {
        String id = CORRELATION_ID.get();
        return id != null ? id : generate();
    }

    public static void setOrGenerate(String headerValue) {
        CORRELATION_ID.set(headerValue != null && !headerValue.isBlank()
                ? headerValue
                : generate());
    }

    public static void clear() {
        CORRELATION_ID.remove();
    }

    private static String generate() {
        return "zs-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
