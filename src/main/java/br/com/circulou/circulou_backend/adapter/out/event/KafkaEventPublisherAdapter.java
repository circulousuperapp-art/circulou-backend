package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.port.out.EventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "circulou.event.publisher.type", havingValue = "kafka")
public class KafkaEventPublisherAdapter implements EventPublisherPort {

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisherAdapter.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(Object event) {
        String topic = determineTopic(event);
        logger.debug("[KAFKA EVENT] Publicando evento no tópico {}: {}", topic, event.getClass().getSimpleName());
        kafkaTemplate.send(topic, event);
    }

    private String determineTopic(Object event) {
        String className = event.getClass().getSimpleName();
        return "circulou.events." + className.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }
}
