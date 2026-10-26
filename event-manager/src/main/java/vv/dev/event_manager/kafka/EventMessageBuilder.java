package vv.dev.event_manager.kafka;

import vv.dev.eventcommon.kafka.ChangeItem;
import vv.dev.eventcommon.kafka.EventChangeKafkaMessage;
import vv.dev.eventcommon.kafka.EventType;
import org.springframework.stereotype.Component;
import vv.dev.event_manager.events.model.Event;
import vv.dev.event_manager.events.model.EventEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class EventMessageBuilder {

    public EventChangeKafkaMessage createUpdateMessage(
            Event event,
            Long changedById,
            List<Long> participants,
            List<ChangeItem> changes
    ) {
        return EventChangeKafkaMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .eventType(EventType.EVENT_UPDATED)
                .eventId(event.getId())
                .eventName(event.getName())
                .occurredAt(LocalDateTime.now())
                .ownerId(event.getOwner().getId())
                .changedById(changedById)
                .subscribers(participants)
                .changes(changes)
                .build();
    }

    public EventChangeKafkaMessage createStatusChangedMessage(
            Event event,
            Long changedById,
            List<Long> participants,
            String oldStatus,
            String newStatus
    ) {
        ChangeItem changeStatus = new ChangeItem(
                "status",
                oldStatus,
                newStatus
        );

        return EventChangeKafkaMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .eventType(EventType.STATUS_CHANGED)
                .eventId(event.getId())
                .eventName(event.getName())
                .occurredAt(LocalDateTime.now())
                .ownerId(event.getOwner().getId())
                .changedById(changedById)
                .subscribers(participants)
                .changes(List.of(changeStatus))
                .build();
    }

    public EventChangeKafkaMessage createStatusChangedMessage(
            EventEntity event,
            Long changedById,
            List<Long> participants,
            String oldStatus,
            String newStatus
    ) {
        ChangeItem changeStatus = new ChangeItem(
                "status",
                oldStatus,
                newStatus
        );

        return EventChangeKafkaMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .eventType(EventType.STATUS_CHANGED)
                .eventId(event.getId())
                .eventName(event.getName())
                .occurredAt(LocalDateTime.now())
                .ownerId(event.getOwner().getId())
                .changedById(changedById)
                .subscribers(participants)
                .changes(List.of(changeStatus))
                .build();
    }
}
