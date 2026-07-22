package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.port.out.EventPublisherPort;
import br.com.circulou.circulou_backend.port.out.MessagePublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;

public class SpringEventPublisherAdapter implements EventPublisherPort, MessagePublisherPort {

    private static final Logger logger = LoggerFactory.getLogger(SpringEventPublisherAdapter.class);
    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringEventPublisherAdapter(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(Object event) {
        logger.debug("[SPRING EVENT] Publicando evento: {}", event.getClass().getSimpleName());
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(String topic, String payload, Map<String, String> headers) {
        logger.debug("[SPRING INFRA] Publicando mensagem no tópico {}: {}", topic, payload);
        // Em Spring Events, o tópico pode ser ignorado ou usado como parte do evento
        applicationEventPublisher.publishEvent(payload);
    }
}
