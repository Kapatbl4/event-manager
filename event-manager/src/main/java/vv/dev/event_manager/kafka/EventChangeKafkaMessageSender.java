package vv.dev.event_manager.kafka;

import VV.dev.eventcommon.kafka.EventChangeKafkaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventChangeKafkaMessageSender {

    private static final Logger log = LoggerFactory.getLogger(EventChangeKafkaMessageSender.class);

    private final KafkaTemplate<Long, EventChangeKafkaMessage> kafkaTemplate;
    private final KafkaTopicsProperties topics;

    public EventChangeKafkaMessageSender(
            KafkaTemplate<Long, EventChangeKafkaMessage> kafkaTemplate,
            KafkaTopicsProperties kafkaTopicsProperties
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topics = kafkaTopicsProperties;
    }

    public void sendMessage(EventChangeKafkaMessage message) {
        String topic = topics.getEventChanges();
        log.info("Sending event changing message: message={}", message);
        kafkaTemplate.send(topic, message.getEventId(), message);
    }
}
