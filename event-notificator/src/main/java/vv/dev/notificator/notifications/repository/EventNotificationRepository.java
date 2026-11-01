package vv.dev.notificator.notifications.repository;

import vv.dev.notificator.notifications.model.EventNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventNotificationRepository extends JpaRepository<EventNotificationEntity, Long> {

    @Query("""
            SELECT e FROM EventNotificationEntity e
            WHERE e.messageId = :messageId
            """)
    Optional<EventNotificationEntity> findByMessageId(@Param("messageId") String messageId);
}
