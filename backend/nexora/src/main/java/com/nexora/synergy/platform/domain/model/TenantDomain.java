package com.nexora.synergy.platform.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object: TenantDomain
 *
 * Encapsulates the tenant's subdomain (e.g., "harvard" from
 * "harvard.zappschool.com"). Self-validates on construction — you can never
 * have an invalid TenantDomain in memory.
 *
 * Business rules enforced here: - Lowercase alphanumeric + hyphens only - 3–63
 * characters - Cannot start or end with a hyphen - Must be globally unique
 * (enforced at repository level)
 */
public final class TenantDomain {

    private static final Pattern VALID_SUBDOMAIN
            = Pattern.compile("^[a-z0-9]([a-z0-9\\-]{1,61}[a-z0-9])?$");

    private final String value;

    private TenantDomain(String value) {
        validate(value);
        this.value = value.toLowerCase();
    }

    public static TenantDomain of(String subdomain) {
        return new TenantDomain(subdomain);
    }

    private void validate(String subdomain) {
        if (subdomain == null || subdomain.isBlank()) {
            throw new IllegalArgumentException("Tenant subdomain must not be blank");
        }
        if (subdomain.length() < 3 || subdomain.length() > 63) {
            throw new IllegalArgumentException(
                    "Tenant subdomain must be 3–63 characters, got: " + subdomain.length());
        }
        if (!VALID_SUBDOMAIN.matcher(subdomain.toLowerCase()).matches()) {
            throw new IllegalArgumentException(
                    "Tenant subdomain must be lowercase alphanumeric with hyphens only: " + subdomain);
        }
        // Reserved subdomains
        if (isReserved(subdomain.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Subdomain is reserved and cannot be used: " + subdomain);
        }
    }

    private boolean isReserved(String sub) {
        return switch (sub) {
            case "www", "api", "admin", "platform", "mail", "smtp", "ftp", "support", "help", "billing", "app", "static", "cdn" ->
                true;
            default ->
                false;
        };
    }

    public String getValue() {
        return value;
    }

    public String toFullDomain(String domainSuffix) {
        return value + domainSuffix;  // e.g., "harvard" + ".zappschool.com"
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TenantDomain other)) {
            return false;
        }
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
