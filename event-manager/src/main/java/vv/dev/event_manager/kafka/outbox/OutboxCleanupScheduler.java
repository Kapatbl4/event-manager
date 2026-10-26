package vv.dev.event_manager.kafka.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OutboxCleanupScheduler {

    private final Logger log = LoggerFactory.getLogger(OutboxCleanupScheduler.class);

    private final OutboxService outboxService;

    @Value("${app.outbox.cleanup.retention-days:14}")
    private int retentionDays;

    public OutboxCleanupScheduler(OutboxService outboxService) {
        this.outboxService = outboxService;
    }

    @Scheduled(cron = "${app.outbox.cleanup.cron:0 0 12 * * TUE}")
    public void deleteOldMessages() {
        log.info("Deleting old outbox messages");
        LocalDateTime deadline = LocalDateTime.now().minusDays(retentionDays);
        outboxService.deleteOldMessages(deadline);
        log.info("Deleting old outbox messages completed");
    }

}
