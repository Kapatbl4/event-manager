package vv.dev.event_manager.kafka.outbox;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import vv.dev.eventcommon.kafka.EventChangeKafkaMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vv.dev.event_manager.kafka.EventChangeKafkaMessageSender;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class OutboxScheduler {

    private final Logger log = LoggerFactory.getLogger(OutboxScheduler.class);

    private final OutboxService outboxService;
    private final EventChangeKafkaMessageSender kafkaMessageSender;
    private final ObjectMapper objectMapper;

    @Value("${app.outbox.limit-first-unsent-messages:10}")
    private int limitUnsentMessages;

    public OutboxScheduler(
            OutboxService outboxService,
            EventChangeKafkaMessageSender kafkaMessageSender,
            ObjectMapper objectMapper
    ) {
        this.outboxService = outboxService;
        this.kafkaMessageSender = kafkaMessageSender;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${app.outbox.kafka.sending-interval:5000}")
    @Transactional
    public void sendToKafka() {
        List<OutboxMessage> messages = outboxService.findFirstXUnsentMessages(limitUnsentMessages);

        if (messages.isEmpty()) {
            return;
        }

        log.info("Processing {} unsent messages from outbox", messages.size());

        for (OutboxMessage message : messages) {
            try {
                EventChangeKafkaMessage kafkaMessage = objectMapper
                        .readValue(message.getPayload(), EventChangeKafkaMessage.class);

                SendResult<Long, EventChangeKafkaMessage> result = kafkaMessageSender
                        .sendMessage(kafkaMessage).get(5, TimeUnit.SECONDS);

                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Message id={} sent to topic={}, partition={}, offset={}",
                        message.getId(),
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset()
                );
                outboxService.markAsSent(message.getId());
                log.info("Message with id: {} sent", message.getId());
            } catch (Exception e) {
                log.error("Failed to send message id: {}", message.getId(), e);
                outboxService.incrementRetryCount(message.getId(), e.getMessage());
            }
        }
    }

}
