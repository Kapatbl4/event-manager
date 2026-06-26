package vv.dev.event_manager.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vv.dev.event_manager.registration.model.RegistrationEntity;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {

    @Query("""
            SELECT COUNT(r) > 0 FROM RegistrationEntity r
            WHERE r.event.id = :eventId AND r.user.id = :userId
            """)
    boolean existsByEventIdAndUserId(Long eventId, Long userId);


    @Query("""
            SELECT r FROM RegistrationEntity r
            WHERE r.event.id = :eventId AND r.user.id = :userId
            """)
    RegistrationEntity findByEventIdAndUserId(Long eventId, Long userId);

    @Query("""
            SELECT r FROM RegistrationEntity r
            WHERE r.user.id = :userId
            """)
    List<RegistrationEntity> findAllByUserId(Long userId);

    @Modifying
    @Query("""
            DELETE FROM RegistrationEntity r
            WHERE r.event.id = :eventId
            """)
    void deleteAllByEventId(@Param("eventId") Long eventId);
}
