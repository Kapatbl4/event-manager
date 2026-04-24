package vv.dev.event_manager.events;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vv.dev.event_manager.events.model.EventEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventStatusScheduler {

    private final EventRepository eventRepository;
    private Map<EventEntity, LocalDateTime> activeEvents = new ConcurrentHashMap<>();

    public EventStatusScheduler(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateStatuses() {
        startEvents();
        finishEvents();
    }

    @PostConstruct
    public void initializeActiveEvents() {
        List<EventEntity> startedEvents = eventRepository.findByStatus("STARTED");
        for (EventEntity entity : startedEvents) {
            LocalDateTime endTime = entity.getDate().plusMinutes(entity.getDuration());
            activeEvents.put(entity, endTime);
        }
    }

    private void startEvents() {
        List<EventEntity> eventsToStart = eventRepository.findEventsToStart();
        for (EventEntity entity : eventsToStart) {
            entity.setStatus("STARTED");
            if (!activeEvents.containsKey(entity)) {
                LocalDateTime endTime = entity.getDate().plusMinutes(entity.getDuration());
                activeEvents.put(entity, endTime);
            }
        }
        if (!eventsToStart.isEmpty()) {
            eventRepository.saveAll(eventsToStart);
        }
    }

    private void finishEvents() {
        List<EventEntity> eventsToFinish = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<EventEntity, LocalDateTime> entry : activeEvents.entrySet()) {
            if (entry.getValue().isBefore(now) || entry.getValue().isEqual(now)) {
                entry.getKey().setStatus("FINISHED");
                eventsToFinish.add(entry.getKey());
            }
        }

        for (EventEntity entity : eventsToFinish) {
            activeEvents.remove(entity);
        }

        if (!eventsToFinish.isEmpty()) {
            eventRepository.saveAll(eventsToFinish);
        }
    }
}
