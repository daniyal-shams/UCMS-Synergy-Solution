package com.nexora.synergy.nexora.application.command;

public record RegisterTenantCommand(
        String domain,
        String adminEmail,
        String adminPassword,
        String planType
        ) {

}
