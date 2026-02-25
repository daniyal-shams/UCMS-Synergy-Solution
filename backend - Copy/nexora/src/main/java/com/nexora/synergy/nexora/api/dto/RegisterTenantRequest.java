package com.nexora.synergy.nexora.api.dto;

public record RegisterTenantRequest(
    String domain,
    String adminEmail,
    String adminPassword
) {

}
