package com.nexora.synergy.platform.application.tenant.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * RegisterTenantCommand — Upgraded with idempotencyKey.
 *
 * idempotencyKey mirrors AWS API Gateway x-amzn-idempotency-key behavior.
 * Clients should generate a UUID per registration attempt and reuse it on
 * retry. This ensures network retries never create duplicate tenants.
 */
public record RegisterTenantCommand(
        @NotBlank(message = "Institution name is required")
        @Size(min = 2, max = 200, message = "Institution name must be 2-200 characters")
        String institutionName,
        @NotBlank(message = "Subdomain is required")
        @Pattern(regexp = "^[a-z0-9][a-z0-9\\-]{1,61}[a-z0-9]$",
                message = "Subdomain must be lowercase alphanumeric with hyphens (3-63 chars)")
        String subdomain,
        @NotBlank(message = "Admin name is required")
        @Size(max = 100)
        String adminName,
        @NotBlank(message = "Admin email is required")
        @Email(message = "Admin email must be valid")
        String adminEmail,
        String adminPhone, // optional

        String idempotencyKey // optional — client-provided for safe retries
        ) {

}
