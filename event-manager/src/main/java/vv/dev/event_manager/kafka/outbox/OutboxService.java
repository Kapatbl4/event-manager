package vv.dev.event_manager.kafka.outbox;

import vv.dev.eventcommon.kafka.EventChangeKafkaMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutboxService {

    private final Logger log = LoggerFactory.getLogger(OutboxService.class);

    private final ObjectMapper objectMapper;
    private final OutboxRepository outboxRepository;

    public OutboxService(
            ObjectMapper objectMapper,
            OutboxRepository outboxRepository
    ) {
        this.objectMapper = objectMapper;
        this.outboxRepository = outboxRepository;
    }

    public void saveMessage(EventChangeKafkaMessage changeKafkaMessage) {
        log.info("Got message to save: {}", changeKafkaMessage);

        OutboxMessage outboxMessage = new OutboxMessage();
        outboxMessage.setMessageId(changeKafkaMessage.getMessageId());
        outboxMessage.setEventType(changeKafkaMessage.getEventType().name());
        outboxMessage.setEventId(changeKafkaMessage.getEventId());
        outboxMessage.setCreatedAt(LocalDateTime.now());
        outboxMessage.setSentAt(null);
        outboxMessage.setRetryCount(0);
        outboxMessage.setLastError(null);

        try {
            outboxMessage.setPayload(objectMapper.writeValueAsString(changeKafkaMessage));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize Kafka message", e);
        }

        outboxRepository.save(outboxMessage);

        log.info("Message saved to outbox: {}", changeKafkaMessage);
    }

    public void markAsSent(Long id) {
        log.info("Got request to mark message as sent, id: {}", id);
        outboxRepository.markAsSent(id);
    }

    public void deleteOldMessages(LocalDateTime deadline) {
        log.info("Got request to delete old messages sent before: {}", deadline);
        outboxRepository.deleteBySentBefore(deadline);
    }

    public void incrementRetryCount(Long id, String error) {
        log.info("Got request to increment retry count for message id: {} end set last error with text: {}", id, error);

        OutboxMessage message = outboxRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Message with id: " + id + " not found"));
        message.setRetryCount(message.getRetryCount() + 1);
        message.setLastError(error);
        outboxRepository.save(message);
    }

    public List<OutboxMessage> findFirstXUnsentMessages(int limit) {
        return outboxRepository.findFirstXUnsentMessages(limit);
    }

}
