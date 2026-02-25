package com.nexora.synergy.shared.outbox.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nexora.synergy.shared.outbox.AllEnums.OutboxStatus;
import com.nexora.synergy.shared.outbox.Entity.OutboxEventEntity;
import com.nexora.synergy.shared.outbox.repository.OutboxRepository;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxPublisher {

    private final OutboxRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public OutboxPublisher(
            OutboxRepository repository,
            RabbitTemplate rabbitTemplate
    ) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishEvents() {

        List<OutboxEventEntity> events
                = repository.findByStatus(OutboxStatus.PENDING);

        for (OutboxEventEntity event : events) {
            try {
                rabbitTemplate.convertAndSend(
                        "platform.exchange",
                        event.getEventType(),
                        event.getPayload()
                );

                event.setStatus(OutboxStatus.SENT);
                event.setProcessedAt(Instant.now());

            } catch (Exception ex) {
                event.setStatus(OutboxStatus.FAILED);
            }
        }
    }
}
