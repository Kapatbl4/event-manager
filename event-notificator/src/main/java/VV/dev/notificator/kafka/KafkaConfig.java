package VV.dev.notificator.kafka;

import VV.dev.eventcommon.kafka.EventChangeKafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public ConsumerFactory<Long, EventChangeKafkaMessage> consumerFactory() {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "notificator-group");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        configProperties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, LongDeserializer.class);
        configProperties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class);

        var factory = new DefaultKafkaConsumerFactory<Long, EventChangeKafkaMessage>(configProperties);

        factory.setValueDeserializer(
                new ErrorHandlingDeserializer<>(
                        new JacksonJsonDeserializer<>(EventChangeKafkaMessage.class, false))
        );

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, EventChangeKafkaMessage> containerFactory(
            ConsumerFactory<Long, EventChangeKafkaMessage> consumerFactory
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<Long, EventChangeKafkaMessage>();
        factory.setConsumerFactory(consumerFactory);

        factory.getContainerProperties().setAckMode(
                ContainerProperties.AckMode.MANUAL
        );

        return factory;
    }

}
