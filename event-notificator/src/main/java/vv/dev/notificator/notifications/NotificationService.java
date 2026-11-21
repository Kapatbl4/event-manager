package vv.dev.notificator.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import vv.dev.eventcommon.kafka.EventChangeKafkaMessage;
import vv.dev.notificator.notifications.model.EventNotificationEntity;
import vv.dev.notificator.notifications.model.EventPayload;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private static final String UNREAD_KEY_PREFIX = "notif:unread:";

    @Value(value = "${app.cache.ttl.notifications}")
    private Duration notificationUnreadCounterTtl;

    private final EventNotificationRepository notificationRepository;
    private final UserEventNotificationRepository userEventNotificationRepository;
    private final NotificationMapper notificationMapper;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${notifications.retention.days:7}")
    private int retentionDays;

    public NotificationService(
            EventNotificationRepository notificationRepository,
            UserEventNotificationRepository userEventNotificationRepository,
            NotificationMapper notificationMapper,
            ObjectMapper objectMapper,
            StringRedisTemplate stringRedisTemplate
    ) {
        this.notificationRepository = notificationRepository;
        this.userEventNotificationRepository = userEventNotificationRepository;
        this.notificationMapper = notificationMapper;
        this.objectMapper = objectMapper;
        this.stringRedisTemplate = stringRedisTemplate;
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
            EventPayload payload = new EventPayload();
            payload.setChanges(message.getChanges());
            payload.setEventName(message.getEventName());
            String payloadJson = objectMapper.writeValueAsString(payload);
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

        for (Long subscriberId : message.getSubscribers()) {
            try {
                stringRedisTemplate.opsForValue().increment(UNREAD_KEY_PREFIX + subscriberId);
            } catch (Exception e) {
                log.warn("Failed to increment unread counter for userId={}", subscriberId, e);
            }
        }
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
        try {
            long unreadCounter = userEventNotificationRepository.countByUserIdAndIsReadFalse(userId);
            stringRedisTemplate.opsForValue().set(
                    UNREAD_KEY_PREFIX + userId,
                    String.valueOf(unreadCounter),
                    notificationUnreadCounterTtl
            );
        } catch (Exception e) {
            log.warn("Failed to sync unread counter for userId={}", userId, e);
        }
    }

    @Transactional
    public void deleteOldNotifications() {
        LocalDateTime deadline = LocalDateTime.now().minusDays(retentionDays);
        userEventNotificationRepository.deleteOldNotifications(deadline);
    }
}
