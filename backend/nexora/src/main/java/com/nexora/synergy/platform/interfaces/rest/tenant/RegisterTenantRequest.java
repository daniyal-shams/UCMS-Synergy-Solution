package com.nexora.synergy.platform.interfaces.rest.tenant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for the tenant registration request body. It decouples the API layer from
 * the application layer's command object.
 */
public record RegisterTenantRequest(
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
        String adminPhone // optional
        ) {

}
