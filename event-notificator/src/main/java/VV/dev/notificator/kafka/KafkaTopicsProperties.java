package VV.dev.notificator.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafka.topic")
public class KafkaTopicsProperties {

    private String eventChanges;

    public String getEventChanges() {
        return eventChanges;
    }

    public void setEventChanges(String eventChanges) {
        this.eventChanges = eventChanges;
    }
}
