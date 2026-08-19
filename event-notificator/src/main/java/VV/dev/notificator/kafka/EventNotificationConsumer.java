package VV.dev.notificator.kafka;

import VV.dev.eventcommon.kafka.EventChangeKafkaMessage;
import VV.dev.notificator.notifications.NotificationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class EventNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventNotificationConsumer.class);
    private final NotificationService notificationService;

    public EventNotificationConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @KafkaListener(
            topics = "${kafka.topic.event-changes}",
            containerFactory = "containerFactory"
    )
    public void listenEvents(
            ConsumerRecord<Long, EventChangeKafkaMessage> consumerRecord,
            Acknowledgment acknowledgment
    ) {
        try {
            EventChangeKafkaMessage message = consumerRecord.value();

            log.info("Received Kafka message: eventId={}, eventType={}, messageId={}",
                    message.getEventId(), message.getEventType(), message.getMessageId());

            notificationService.saveNotification(message);

            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process message {}", consumerRecord.value(), e);

            // TODO: В реальном проекте здесь был бы DLQ
            // Отправляем в Dead Letter Queue для ручного разбора
            // kafkaTemplate.send(KafkaTopics.EVENT_CHANGES_DLQ, consumerRecord.value());

            // Пока просто подтверждаем, чтобы не блокировать очередь
            acknowledgment.acknowledge();
        }
    }
}
