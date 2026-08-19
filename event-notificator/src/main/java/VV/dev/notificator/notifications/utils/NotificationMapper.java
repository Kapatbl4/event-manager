package VV.dev.notificator.notifications.utils;

import VV.dev.notificator.notifications.model.UserEventNotificationDTO;
import VV.dev.notificator.notifications.model.UserEventNotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    private final MessageBuilder messageBuilder;

    public NotificationMapper(MessageBuilder messageBuilder) {
        this.messageBuilder = messageBuilder;
    }

    public UserEventNotificationDTO mapToDTO(UserEventNotificationEntity userEventNotificationEntity) {
        return new UserEventNotificationDTO(
                userEventNotificationEntity.getId(),
                userEventNotificationEntity.getNotification().getEventType(),
                userEventNotificationEntity.getEventId(),
                userEventNotificationEntity.getCreatedAt(),
                userEventNotificationEntity.isRead(),
                messageBuilder.buildMessage(userEventNotificationEntity.getNotification()),
                userEventNotificationEntity.getNotification().getPayload()
        );
    }

}
