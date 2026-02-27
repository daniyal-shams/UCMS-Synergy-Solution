package com.nexora.synergy.platform.application.tenant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.synergy.infrastructure.outbox.IdempotencyRepository;
import com.nexora.synergy.infrastructure.outbox.OutboxEventPublisher;
import com.nexora.synergy.platform.domain.repository.TenantRepository;
import com.nexora.synergy.platform.domain.tenant.TenantFactory;

import org.springframework.beans.factory.annotation.Value;

import com.nexora.synergy.exception.IdempotencyConflictException;
import com.nexora.synergy.exception.TenantDomainAlreadyExistsException;
import com.nexora.synergy.infrastructure.correlation.CorrelationContext;
import com.nexora.synergy.infrastructure.outbox.IdempotencyStore;
import com.nexora.synergy.platform.application.tenant.command.RegisterTenantCommand;
import com.nexora.synergy.platform.application.tenant.command.RegisterTenantResult;
import com.nexora.synergy.platform.domain.model.Tenant;
import com.nexora.synergy.platform.domain.model.TenantDomain;

import io.micrometer.core.annotation.Timed;

/**
 * RegisterTenantApplicationService — Upgraded to AWS standards.
 *
 * UPGRADES from v1: 1. Idempotency — duplicate requests with same key return
 * cached result 2. Outbox — events persisted IN the transaction (not after) 3.
 * Metrics — tenant.registration.* metrics emitted (like CloudWatch custom
 * metrics) 4. Correlation ID — propagated from HTTP request through all events
 * 5. Structured operation log — every field explicitly named in log
 *
 * Transaction boundary: Everything in execute() runs in ONE transaction: -
 * Idempotency record creation - Domain uniqueness check - Tenant aggregate
 * creation - tenantRepository.save() - outboxRepository.save() (events) If ANY
 * step fails, everything rolls back. This is the "exactly-once write"
 * guarantee.
 */
@Service
@Transactional
public class RegisterTenantApplicationService {

    private static final Logger log = LoggerFactory.getLogger(RegisterTenantApplicationService.class);

    private final TenantFactory tenantFactory;
    private final TenantRepository tenantRepository;
    private final OutboxEventPublisher outboxEventPublisher;
    private final IdempotencyRepository idempotencyRepository;
    private final MeterRegistry meterRegistry;
    private final ObjectMapper objectMapper;

    @Value("${zappschool.tenant.domain-suffix:.zappschool.com}")
    private String domainSuffix;

    @Value("${zappschool.idempotency.ttl-minutes:60}")
    private long idempotencyTtlMinutes;

    public RegisterTenantApplicationService(
            TenantFactory tenantFactory,
            TenantRepository tenantRepository,
            OutboxEventPublisher outboxEventPublisher,
            IdempotencyRepository idempotencyRepository,
            MeterRegistry meterRegistry,
            ObjectMapper objectMapper
    ) {
        this.tenantFactory = tenantFactory;
        this.tenantRepository = tenantRepository;
        this.outboxEventPublisher = outboxEventPublisher;
        this.idempotencyRepository = idempotencyRepository;
        this.meterRegistry = meterRegistry;
        this.objectMapper = objectMapper;
    }

    @Timed(value = "tenant.registration", description = "Time taken to register a tenant")
    public RegisterTenantResult execute(RegisterTenantCommand command) {
        String correlationId = CorrelationContext.get();
        String idempotencyKey = command.idempotencyKey();

        log.info("RegisterTenant.start: subdomain={} institution={} correlationId={}",
                command.subdomain(), command.institutionName(), correlationId);

        // ── Step 1: Idempotency check (like API Gateway) ──
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existing = idempotencyRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                IdempotencyStore rec = existing.get();
                if (rec.isExpired()) {
                    idempotencyRepository.delete(rec); // Clean up, proceed as new
                } else if (rec.isInProgress()) {
                    throw new IdempotencyConflictException(idempotencyKey);
                } else if (rec.isComplete()) {
                    log.info("RegisterTenant.idempotent: returning cached result for key={}", idempotencyKey);
                    meterRegistry.counter("tenant.registration.idempotent").increment();
                    return deserializeResult(rec.getResponsePayload());
                }
            }
        }

        // ── Step 2: Create idempotency record (mark IN_PROGRESS) ──
        IdempotencyStore idempotencyRecord = null;
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyRecord = IdempotencyStore.createInProgress(
                    idempotencyKey, "REGISTER_TENANT", idempotencyTtlMinutes);
            idempotencyRepository.save(idempotencyRecord);
        }

        try {
            // ── Step 3: Domain uniqueness guard ──
            TenantDomain domain = TenantDomain.of(command.subdomain());
            if (tenantRepository.existsByDomain(domain)) {
                meterRegistry.counter("tenant.registration.conflict").increment();
                throw new TenantDomainAlreadyExistsException(
                        "Subdomain already registered: " + command.subdomain());
            }

            // ── Step 4: Create Tenant aggregate via Factory ──
            Tenant tenant = tenantFactory.createNew(
                    command.subdomain(),
                    command.institutionName(),
                    command.adminName(),
                    command.adminEmail(),
                    command.adminPhone(),
                    correlationId
            );

            // ── Step 5: Persist aggregate ──
            Tenant saved = tenantRepository.save(tenant);

            // ── Step 6: Persist domain events to Outbox (SAME transaction) ──
            // If save() succeeded but app crashes here, Outbox row is also rolled back.
            // No orphaned events.
            saved.getDomainEvents()
                    .forEach(event -> outboxEventPublisher.publish(event, "Tenant"));
            saved.clearEvents();

            log.info("RegisterTenant.complete: tenantId={} subdomain={} correlationId={}",
                    saved.getId(), saved.getDomain().getValue(), correlationId);
            meterRegistry.counter("tenant.registration.success").increment();

            RegisterTenantResult result = new RegisterTenantResult(
                    saved.getId().toString(),
                    saved.getDomain().getValue(),
                    saved.getInstitutionName(),
                    saved.getDomain().toFullDomain(domainSuffix),
                    saved.getStatus(),
                    saved.getRegisteredAt()
            );

            // ── Step 7: Mark idempotency record COMPLETE ──
            if (idempotencyRecord != null) {
                idempotencyRecord.complete(serializeResult(result));
                idempotencyRepository.save(idempotencyRecord);
            }

            return result;

        } catch (Exception ex) {
            meterRegistry.counter("tenant.registration.error",
                    "type", ex.getClass().getSimpleName()).increment();
            throw ex;
        }
    }

    private String serializeResult(RegisterTenantResult result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            return "{}";
        }
    }

    private RegisterTenantResult deserializeResult(String json) {
        try {
            return objectMapper.readValue(json, RegisterTenantResult.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize cached result");
        }
    }

}
