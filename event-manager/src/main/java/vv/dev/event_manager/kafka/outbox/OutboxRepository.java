package vv.dev.event_manager.kafka.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {

    @Query("""
            SELECT o FROM OutboxMessage o
            WHERE o.sentAt IS NULL
            ORDER BY o.createdAt
            LIMIT :limit
            """)
    List<OutboxMessage> findFirstXUnsentMessages(@Param("limit") int limit);


    @Modifying
    @Query("""
            UPDATE OutboxMessage o
            SET o.sentAt = CURRENT_TIMESTAMP
            WHERE o.id = :id
            """)
    void markAsSent(@Param("id") Long id);

    @Modifying
    @Query("""
            DELETE FROM OutboxMessage o
            WHERE o.sentAt < :deadline
            """)
    public void deleteBySentBefore(@Param("deadline") LocalDateTime deadline);
}
