package vv.dev.eventcommon.kafka;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public class EventChangeKafkaMessage {
    private String messageId;
    private EventType eventType;
    private Long eventId;
    private String eventName;
    private LocalDateTime occurredAt;
    private Long ownerId;
    private Long changedById;
    private List<Long> subscribers;
    private List<ChangeItem> changes;

    public EventChangeKafkaMessage() {
    }

    public EventChangeKafkaMessage(
            String messageId,
            EventType eventType,
            Long eventId,
            String eventName,
            LocalDateTime occurredAt,
            Long ownerId,
            Long changedById,
            List<Long> subscribers,
            List<ChangeItem> changes
    ) {
        this.messageId = messageId;
        this.eventType = eventType;
        this.eventId = eventId;
        this.eventName = eventName;
        this.occurredAt = occurredAt;
        this.ownerId = ownerId;
        this.changedById = changedById;
        this.subscribers = subscribers;
        this.changes = changes;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getChangedById() {
        return changedById;
    }

    public void setChangedById(Long changedById) {
        this.changedById = changedById;
    }

    public List<Long> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<Long> subscribers) {
        this.subscribers = subscribers;
    }

    public List<ChangeItem> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangeItem> changes) {
        this.changes = changes;
    }

}
