package com.nexora.synergy.platform.domain.model;

import java.util.Set;

import com.nexora.synergy.exception.InvalidTenantStateTransitionException;

/**
 * Tenant Lifecycle States
 *
 * PENDING → Registered, provisioning not yet started PROVISIONING→ DB schema
 * being created ACTIVE → Fully operational SUSPENDED → Subscription lapsed /
 * manual admin action TERMINATED → Permanently closed, data retained per
 * retention policy FAILED → Provisioning failed — admin must intervene
 *
 * Transitions are enforced here — not in application logic. This is a core
 * domain invariant.
 */
public enum TenantStatus {

    PENDING {
        @Override
        public Set<TenantStatus> allowedTransitions() {
            return Set.of(PROVISIONING, FAILED);
        }
    },
    PROVISIONING {
        @Override
        public Set<TenantStatus> allowedTransitions() {
            return Set.of(ACTIVE, FAILED);
        }
    },
    ACTIVE {
        @Override
        public Set<TenantStatus> allowedTransitions() {
            return Set.of(SUSPENDED, TERMINATED);
        }
    },
    SUSPENDED {
        @Override
        public Set<TenantStatus> allowedTransitions() {
            return Set.of(ACTIVE, TERMINATED);
        }
    },
    FAILED {
        @Override
        public Set<TenantStatus> allowedTransitions() {
            return Set.of(PENDING); // Allow retry from FAILED → PENDING
        }
    },
    TERMINATED {
        @Override
        public Set<TenantStatus> allowedTransitions() {
            return Set.of(); // Terminal state — no transitions allowed
        }
    };

    public abstract Set<TenantStatus> allowedTransitions();

    /**
     * Validate and return the new status. Throws if transition is not
     * permitted.
     */
    public TenantStatus transitionTo(TenantStatus next) {
        if (!allowedTransitions().contains(next)) {
            throw new InvalidTenantStateTransitionException(
                    String.format("Cannot transition tenant from %s to %s", this.name(), next.name())
            );
        }
        return next;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isOperational() {
        return this == ACTIVE;
    }
}
