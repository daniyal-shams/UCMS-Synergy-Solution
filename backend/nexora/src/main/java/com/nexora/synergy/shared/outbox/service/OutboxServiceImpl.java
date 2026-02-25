package com.nexora.synergy.shared.outbox.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.synergy.shared.outbox.AllEnums.OutboxStatus;
import com.nexora.synergy.shared.outbox.Entity.OutboxEventEntity;
import com.nexora.synergy.shared.outbox.repository.OutboxRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class OutboxServiceImpl implements OutboxService {

    private final OutboxRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxServiceImpl(
            OutboxRepository repository,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void saveEvent(String eventType, String payload) {

        OutboxEventEntity event = new OutboxEventEntity();
        event.setId(UUID.randomUUID());
        event.setEventType(eventType);
        event.setPayload(payload);
        event.setStatus(OutboxStatus.PENDING);
        event.setCreatedAt(Instant.now());

        repository.save(event);
    }
}
