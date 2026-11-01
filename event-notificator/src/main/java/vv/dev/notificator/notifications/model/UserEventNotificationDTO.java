package vv.dev.notificator.notifications.model;

import vv.dev.eventcommon.kafka.EventType;

import java.time.LocalDateTime;

public class UserEventNotificationDTO {

    private Long id;
    private EventType type;
    private Long eventId;
    private LocalDateTime createdAt;
    private boolean isRead;
    private String message;
    private EventPayload payload;

    public UserEventNotificationDTO(
            Long id,
            EventType type,
            Long eventId,
            LocalDateTime createdAt,
            boolean isRead,
            String message,
            EventPayload payload
    ) {
        this.id = id;
        this.type = type;
        this.eventId = eventId;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.message = message;
        this.payload = payload;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EventPayload getPayload() {
        return payload;
    }

    public void setPayload(EventPayload payload) {
        this.payload = payload;
    }
}
