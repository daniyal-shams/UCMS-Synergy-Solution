package com.nexora.synergy.shared.outbox.service;

public interface OutboxService {

    void saveEvent(String eventType, String payload);
}
