package vv.dev.notificator.notifications;

import vv.dev.eventcommon.kafka.EventChangeKafkaMessage;
import vv.dev.notificator.notifications.model.EventNotificationEntity;
import vv.dev.notificator.notifications.model.UserEventNotificationDTO;
import vv.dev.notificator.notifications.model.UserEventNotificationEntity;
import vv.dev.notificator.notifications.repository.EventNotificationRepository;
import vv.dev.notificator.notifications.repository.UserEventNotificationRepository;
import vv.dev.notificator.notifications.utils.NotificationMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final EventNotificationRepository notificationRepository;
    private final UserEventNotificationRepository userEventNotificationRepository;
    private final NotificationMapper notificationMapper;
    private final ObjectMapper objectMapper;

    @Value("${notifications.retention.days:7}")
    private int retentionDays;

    public NotificationService(
            EventNotificationRepository notificationRepository,
            UserEventNotificationRepository userEventNotificationRepository,
            NotificationMapper notificationMapper,
            ObjectMapper objectMapper
    ) {
        this.notificationRepository = notificationRepository;
        this.userEventNotificationRepository = userEventNotificationRepository;
        this.notificationMapper = notificationMapper;
        this.objectMapper = objectMapper;
    }

    public void saveNotification(EventChangeKafkaMessage message) {
        var foundNotification = notificationRepository.findByMessageId(message.getMessageId());
        if (foundNotification.isPresent()) {
            return;
        }
        EventNotificationEntity entity = new EventNotificationEntity();
        entity.setMessageId(message.getMessageId());
        entity.setEventType(message.getEventType());
        entity.setEventId(message.getEventId());
        entity.setOccurredAt(message.getOccurredAt());
        entity.setOwnerId(message.getOwnerId());
        entity.setChangedById(message.getChangedById());

        try {
            String payloadJson = objectMapper.writeValueAsString(message.getChanges());
            entity.setPayload(payloadJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize Kafka message", e);
        }

        List<UserEventNotificationEntity> userEventNotificationEntities = new ArrayList<>();
        for (Long subscriberId : message.getSubscribers()) {
            UserEventNotificationEntity userEntity = new UserEventNotificationEntity();
            userEntity.setUserId(subscriberId);
            userEntity.setEventId(message.getEventId());
            userEntity.setNotification(entity);
            userEntity.setRead(false);
            userEntity.setReadAt(null);
            userEntity.setCreatedAt(LocalDateTime.now());
            userEventNotificationEntities.add(userEntity);
        }

        entity.setUserNotifications(userEventNotificationEntities);

        notificationRepository.save(entity);
    }

    public List<UserEventNotificationDTO> getUserNotifications(Long userId) {
        List<UserEventNotificationEntity> entities = userEventNotificationRepository.findAllByUserIdAndIsReadFalse(userId);

        return entities.stream()
                .map(notificationMapper::mapToDTO)
                .toList();
    }

    @Transactional
    public void markNotificationsAsRead(Long userId, List<Long> notificationsIds) {
        List<UserEventNotificationEntity> entities = userEventNotificationRepository
                .findAllByIdInAndUserId(notificationsIds, userId);
        for (UserEventNotificationEntity entity : entities) {
            if (!entity.isRead()) {
                entity.setRead(true);
                entity.setReadAt(LocalDateTime.now());
            }
        }
        userEventNotificationRepository.saveAll(entities);
    }

    @Transactional
    public void deleteOldNotifications() {
        LocalDateTime deadline = LocalDateTime.now().minusDays(retentionDays);
        userEventNotificationRepository.deleteOldNotifications(deadline);
    }
}
