package vv.dev.event_manager.location;

import org.springframework.data.jpa.repository.JpaRepository;
import vv.dev.event_manager.location.model.EventLocationEntity;

public interface EventLocationRepository extends JpaRepository<EventLocationEntity, Long> {

    boolean existsByName(String name);
}
