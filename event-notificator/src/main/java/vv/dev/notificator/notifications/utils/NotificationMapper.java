package vv.dev.notificator.notifications.utils;

import vv.dev.notificator.notifications.model.EventPayload;
import vv.dev.notificator.notifications.model.UserEventNotificationDTO;
import vv.dev.notificator.notifications.model.UserEventNotificationEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    private final MessageBuilder messageBuilder;
    private final ObjectMapper objectMapper;

    private final Logger log = LoggerFactory.getLogger(NotificationMapper.class);

    public NotificationMapper(MessageBuilder messageBuilder, ObjectMapper objectMapper) {
        this.messageBuilder = messageBuilder;
        this.objectMapper = objectMapper;
    }

    public UserEventNotificationDTO mapToDTO(UserEventNotificationEntity userEventNotificationEntity) {
        EventPayload payload = null;

        try {
            String payloadJson = userEventNotificationEntity.getNotification().getPayload();
            if (payloadJson != null && !payloadJson.isBlank()) {
                payload = objectMapper.readValue(payloadJson, EventPayload.class);
            }
        } catch (Exception e) {
            log.warn("Failed to deserialize payload for notification {}", userEventNotificationEntity.getId());
        }

        return new UserEventNotificationDTO(
                userEventNotificationEntity.getId(),
                userEventNotificationEntity.getNotification().getEventType(),
                userEventNotificationEntity.getEventId(),
                userEventNotificationEntity.getCreatedAt(),
                userEventNotificationEntity.isRead(),
                messageBuilder.buildMessage(userEventNotificationEntity.getNotification()),
                payload
        );
    }

}
