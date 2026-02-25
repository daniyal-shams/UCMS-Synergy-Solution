package com.nexora.synergy.nexora.api.Onboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.synergy.nexora.api.dto.RegisterTenantRequest;
import com.nexora.synergy.nexora.api.dto.RegisterTenantResponse;
import com.nexora.synergy.nexora.application.command.RegisterTenantCommand;
import com.nexora.synergy.nexora.application.service.RegisterTenantUseCase;
import com.nexora.synergy.nexora.domain.valueObject.TenantId;

@RestController
@RequestMapping("/nexora")
public class TenantController {

    private final RegisterTenantUseCase registerTenantUseCase;

    public TenantController(RegisterTenantUseCase registerTenantUseCase) {
        this.registerTenantUseCase = registerTenantUseCase;
    }

    /*
     * Create new tenant (Organisation or trust).
     */
    @PostMapping("/tenant/register")
    public ResponseEntity<RegisterTenantResponse> registerTenant(@RequestBody RegisterTenantRequest request) {

        TenantId tenantId = registerTenantUseCase.execute(
                new RegisterTenantCommand(
                        request.domain(),
                        request.adminEmail(),
                        request.adminPassword(),
                        "INACTIVE"
                )
        );

        return ResponseEntity.ok(new RegisterTenantResponse(tenantId.value(), "CREATED"));

    }

    @PostMapping("/{tenantId}/billing/process")
    public ResponseEntity<BillingProcessResponse> billingProcess(@RequestBody BillingProcessRequest request) {

        return ResponseEntity.ok();

    }

    @PostMapping("/{tenantId}/campus/register")
    public ResponseEntity<CampusRegisterResponse> campusRegister(@RequestBody CampusRegisterRequest request
    ) {

        return ResponseEntity.ok();

    }

}
