package vv.dev.event_manager.events;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vv.dev.event_manager.events.model.EventEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventStatusScheduler {

    private final EventRepository eventRepository;

    public EventStatusScheduler(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateStatuses() {
        startEvents();
        finishEvents();
    }

    private void startEvents() {
        List<EventEntity> eventsToStart = eventRepository.findEventsToStart();
        for (EventEntity entity : eventsToStart) {
            entity.setStatus("STARTED");
        }
        if (!eventsToStart.isEmpty()) {
            eventRepository.saveAll(eventsToStart);
        }
    }

    private void finishEvents() {
        List<EventEntity> startedEvents = eventRepository.findByStatus("STARTED");
        List<EventEntity> eventsToFinish = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (EventEntity entity : startedEvents) {
            LocalDateTime endTime = entity.getDate().plusMinutes(entity.getDuration());
            if (endTime.isBefore(now) || endTime.isEqual(now)) {
                entity.setStatus("FINISHED");
                eventsToFinish.add(entity);
            }
        }

        if (!eventsToFinish.isEmpty()) {
            eventRepository.saveAll(eventsToFinish);
        }
    }
}
