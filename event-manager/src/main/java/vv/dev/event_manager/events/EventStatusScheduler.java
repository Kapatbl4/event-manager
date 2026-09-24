package vv.dev.event_manager.events;

import vv.dev.eventcommon.kafka.EventChangeKafkaMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vv.dev.event_manager.events.model.EventEntity;
import vv.dev.event_manager.kafka.EventChangeKafkaMessageSender;
import vv.dev.event_manager.kafka.EventMessageBuilder;
import vv.dev.event_manager.kafka.outbox.OutboxService;
import vv.dev.event_manager.registration.RegistrationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventStatusScheduler {

    private final EventRepository eventRepository;
    private final OutboxService outboxService;
    private final EventMessageBuilder eventMessageBuilder;
    private final RegistrationService registrationService;

    public EventStatusScheduler(
            EventRepository eventRepository,
            OutboxService outboxService,
            EventMessageBuilder eventMessageBuilder,
            RegistrationService registrationService
    ) {
        this.eventRepository = eventRepository;
        this.outboxService = outboxService;
        this.eventMessageBuilder = eventMessageBuilder;
        this.registrationService = registrationService;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateStatuses() {
        startEvents();
        finishEvents();
    }

    private void startEvents() {
        List<EventEntity> eventsToStart = eventRepository.findEventsToStart();
        List<EventChangeKafkaMessage> messages = new ArrayList<>();
        for (EventEntity entity : eventsToStart) {
            String oldStatus = entity.getStatus();
            entity.setStatus("STARTED");
            List<Long> participants = registrationService.findAllEventParticipants(entity.getId());
            EventChangeKafkaMessage message = eventMessageBuilder.createStatusChangedMessage(
                    entity,
                    null,
                    participants,
                    oldStatus,
                    entity.getStatus()
            );
            messages.add(message);
        }
        if (!eventsToStart.isEmpty()) {
            eventRepository.saveAll(eventsToStart);
            for (EventChangeKafkaMessage message : messages) {
                outboxService.saveMessage(message);
            }
        }
    }

    private void finishEvents() {
        List<EventEntity> startedEvents = eventRepository.findByStatus("STARTED");
        List<EventEntity> eventsToFinish = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        List<EventChangeKafkaMessage> messages = new ArrayList<>();

        for (EventEntity entity : startedEvents) {
            LocalDateTime endTime = entity.getDate().plusMinutes(entity.getDuration());
            if (endTime.isBefore(now) || endTime.isEqual(now)) {
                String oldStatus = entity.getStatus();
                entity.setStatus("FINISHED");
                eventsToFinish.add(entity);
                List<Long> participants = registrationService.findAllEventParticipants(entity.getId());
                EventChangeKafkaMessage message = eventMessageBuilder.createStatusChangedMessage(
                        entity,
                        null,
                        participants,
                        oldStatus,
                        entity.getStatus()
                );
                messages.add(message);
            }
        }

        if (!eventsToFinish.isEmpty()) {
            eventRepository.saveAll(eventsToFinish);
            for (EventChangeKafkaMessage message : messages) {
                outboxService.saveMessage(message);
            }
        }
    }
}
