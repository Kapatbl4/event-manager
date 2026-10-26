package vv.dev.event_manager.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafka.topic")
public class KafkaTopicsProperties {

    private String eventChanges;
    private String dlq;

    public String getEventChanges() {
        return eventChanges;
    }

    public void setEventChanges(String eventChanges) {
        this.eventChanges = eventChanges;
    }

    public String getDlq() {
        return dlq;
    }

    public void setDlq(String dlq) {
        this.dlq = dlq;
    }
}
