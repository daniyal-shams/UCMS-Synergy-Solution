package com.nexora.synergy.infrastructure.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.nexora.synergy.platform.domain.shared.events.DomainEvent;

/**
 * OutboxEventPublisher — serializes domain events into the outbox table.
 *
 * MUST be called inside the same @Transactional as aggregate.save(). If the
 * outer transaction rolls back, this record rolls back too. This is the core of
 * the Transactional Outbox guarantee.
 */
@Component
public class OutboxEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventPublisher.class);

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxEventPublisher(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    public void publish(DomainEvent event, String aggregateType) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            OutboxMessage msg = OutboxMessage.create(
                    aggregateType, event.getAggregateId(), event.getEventType(),
                    payload, event.getCorrelationId(), event.getEventId());
            outboxRepository.save(msg);
            log.debug("Outbox.persist: type={} aggregateId={} correlationId={}",
                    event.getEventType(), event.getAggregateId(), event.getCorrelationId());
        } catch (Exception e) {
            throw new RuntimeException("Outbox write failed for: " + event.getEventType(), e);
        }
    }
}
