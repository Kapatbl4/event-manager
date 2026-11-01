package vv.dev.notificator.notifications.repository;


import vv.dev.notificator.notifications.model.UserEventNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserEventNotificationRepository extends JpaRepository<UserEventNotificationEntity, Long> {

    List<UserEventNotificationEntity> findAllByUserId(Long userId);

    List<UserEventNotificationEntity> findAllByUserIdAndIsReadFalse(Long userId);

    @Modifying
    @Query("""
           DELETE FROM UserEventNotificationEntity e
           WHERE e.createdAt < :deadline
           """)
    void deleteOldNotifications(@Param("deadline") LocalDateTime deadline);

    List<UserEventNotificationEntity> findAllByIdInAndUserId(
            List<Long> ids,
            Long userId
    );
}
