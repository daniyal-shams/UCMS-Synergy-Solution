package com.nexora.synergy.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxMessage, UUID> {

    /**
     * FOR UPDATE SKIP LOCKED — prevents concurrent instances grabbing the same
     * row. Equivalent to SQS visibility timeout on in-flight messages.
     */
    @Query(value = """
        SELECT * FROM platform.outbox_messages
        WHERE status = 'PENDING' AND next_retry_at <= :now
        ORDER BY created_at ASC LIMIT :limit
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<OutboxMessage> findPendingForDispatch(@Param("now") Instant now, @Param("limit") int limit);

    Optional<OutboxMessage> findByIdempotencyKey(String idempotencyKey);

    @Query("SELECT COUNT(m) FROM OutboxMessage m WHERE m.status = 'PENDING'")
    long countPending();

    @Query("SELECT COUNT(m) FROM OutboxMessage m WHERE m.status = 'DEAD_LETTER'")
    long countDeadLettered();
}
