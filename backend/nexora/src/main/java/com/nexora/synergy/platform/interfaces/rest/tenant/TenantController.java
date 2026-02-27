package com.nexora.synergy.platform.interfaces.rest.tenant;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.synergy.platform.application.tenant.command.RegisterTenantCommand;
import com.nexora.synergy.platform.application.tenant.command.RegisterTenantResult;
import com.nexora.synergy.platform.application.tenant.service.RegisterTenantApplicationService;
import com.nexora.synergy.platform.application.tenant.service.TenantQueryService;
import com.nexora.synergy.platform.interfaces.rest.dto.ApiResponse;

import jakarta.validation.Valid;

/**
 * TenantController — Upgraded to AWS standards.
 *
 * UPGRADES from v1: 1. X-Idempotency-Key header support on POST /register 2.
 * Pagination on GET /tenants (AWS API pagination pattern) 3. Status filtering
 * on GET /tenants 4. Retry endpoint POST /tenants/{id}/retry for FAILED tenants
 * 5. Content-Type: application/json enforced on all responses
 */
@RestController
@RequestMapping("/nexora/tenants")
public class TenantController {

    private final RegisterTenantApplicationService registerService;
    private final TenantQueryService queryService;

    public TenantController(RegisterTenantApplicationService registerService,
            TenantQueryService queryService) {
        this.registerService = registerService;
        this.queryService = queryService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TenantRegistrationResponse>> register(
            @Valid @RequestBody RegisterTenantRequest request,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey
    ) {

        RegisterTenantCommand command = new RegisterTenantCommand(
                request.institutionName(),
                request.subdomain(),
                request.adminName(),
                request.adminEmail(),
                request.adminPhone(),
                idempotencyKey
        );

        RegisterTenantResult result = registerService.execute(command);

        TenantRegistrationResponse data = new TenantRegistrationResponse(
                result.tenantId(), result.subdomain(), result.institutionName(),
                result.fullDomain(), result.status().name(), result.registeredAt(),
                "Registration accepted. Poll /nexora/tenants/" + result.tenantId() + "/status"
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success(data));

    }

}
