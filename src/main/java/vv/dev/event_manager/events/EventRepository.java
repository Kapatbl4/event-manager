package vv.dev.event_manager.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vv.dev.event_manager.events.model.EventEntity;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {
    boolean existsByName(String name);

    @Query("""
            SELECT e FROM EventEntity e
            WHERE e.location.id = :locationId
            AND (e.date > CURRENT_TIMESTAMP OR e.status = 'STARTED')
            """)
    List<EventEntity> findEventsByLocationAndInTheFutureOrPresent(
            @Param("locationId") Long locationId
    );

    @Query("""
            SELECT e FROM EventEntity e
            WHERE e.owner.id = :userId
            """)
    List<EventEntity> findAllByUserId(@Param("userId") Long id);

    @Query("""
            SELECT e FROM EventEntity e
            WHERE e.status = 'WAIT_START' AND e.date <= CURRENT_TIMESTAMP
            """)
    List<EventEntity> findEventsToStart();

    @Query("""
            SELECT e FROM EventEntity e
            WHERE e.status = :status
            """)
    List<EventEntity> findByStatus(@Param("status") String status);
}
