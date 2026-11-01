package vv.dev.notificator.notifications.utils;

import vv.dev.eventcommon.kafka.EventType;
import vv.dev.notificator.notifications.model.EventNotificationEntity;
import vv.dev.notificator.notifications.model.EventPayload;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MessageBuilder {

    private final ObjectMapper objectMapper;

    public MessageBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String buildMessage(EventNotificationEntity entity) {
        try {
            EventPayload payload = objectMapper.readValue(
                    entity.getPayload(),
                    EventPayload.class
            );
            String eventName = payload.getEventName();
            EventType type = entity.getEventType();

            return switch (type) {
                case STATUS_CHANGED -> "Статус мероприятия '" + eventName + "' изменён";
                default -> "Мероприятие '" + eventName + "' обновлено";
            };

        } catch (Exception e) {
            return "Мероприятие обновлено";
        }
    }
}
