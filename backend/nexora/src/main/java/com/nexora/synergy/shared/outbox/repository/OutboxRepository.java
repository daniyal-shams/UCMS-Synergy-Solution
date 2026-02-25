package com.nexora.synergy.shared.outbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexora.synergy.shared.outbox.AllEnums.OutboxStatus;
import com.nexora.synergy.shared.outbox.Entity.OutboxEventEntity;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository
        extends JpaRepository<OutboxEventEntity, UUID> {

    List<OutboxEventEntity> findByStatus(OutboxStatus status);
}
