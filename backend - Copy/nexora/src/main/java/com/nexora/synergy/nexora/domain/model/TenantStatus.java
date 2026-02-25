package com.nexora.synergy.nexora.domain.model;

public enum TenantStatus {

    ONHOLD, // pending activation
    PENDING, // awaiting provisioning
    REQUESTED, // registered but not provisioned
    PROVISIONING, // infrastructure creation in progress
    ACTIVE, // ready for use
    FAILED, // provisioning failed
    SUSPENDED, // billing or admin suspension
    DELETED        // soft delete

}
