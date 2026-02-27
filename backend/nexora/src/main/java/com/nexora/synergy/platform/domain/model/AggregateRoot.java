package com.nexora.synergy.platform.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nexora.synergy.platform.domain.shared.events.DomainEvent;

/**
 * Base AggregateRoot — collects domain events during a transaction.
 *
 * AWS First-Principle: Events are the source of truth for state changes. Every
 * mutation must produce a traceable, typed event.
 *
 * Events are held in memory until the Application Service persists the
 * aggregate, then published transactionally via the Outbox pattern. This
 * guarantees at-least-once delivery even on process crash.
 */
public abstract class AggregateRoot {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public void clearEvents() {
        domainEvents.clear();
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public boolean hasEvents() {
        return !domainEvents.isEmpty();
    }
}
