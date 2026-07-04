package vv.dev.event_manager.kafka;

import VV.dev.eventcommon.kafka.ChangeItem;
import VV.dev.eventcommon.kafka.EventChangeKafkaMessage;
import VV.dev.eventcommon.kafka.EventType;
import org.springframework.stereotype.Component;
import vv.dev.event_manager.events.model.Event;

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
            List<ChangeItem> changes
    ) {
        return EventChangeKafkaMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .eventType(EventType.STATUS_CHANGED)
                .eventId(event.getId())
                .occurredAt(LocalDateTime.now())
                .ownerId(event.getOwner().getId())
                .changedById(changedById)
                .subscribers(participants)
                .changes(changes)
                .build();
    }
}
