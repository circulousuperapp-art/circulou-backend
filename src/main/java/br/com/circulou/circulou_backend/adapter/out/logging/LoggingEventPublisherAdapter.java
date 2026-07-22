package br.com.circulou.circulou_backend.adapter.out.logging;

import br.com.circulou.circulou_backend.port.out.EventPublisherPort;
import br.com.circulou.circulou_backend.port.out.MessagePublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

public class LoggingEventPublisherAdapter implements EventPublisherPort, MessagePublisherPort {

    private static final Logger logger = LoggerFactory.getLogger(LoggingEventPublisherAdapter.class);

    @Override
    public void publish(Object event) {
        logger.info("[DOMAIN EVENT] Publicando: {} - Conteúdo: {}", event.getClass().getSimpleName(), event);
    }

    @Override
    public void publish(String topic, String payload, Map<String, String> headers) {
        logger.info("[INFRA MESSAGE] Publicando no tópico: {} - Payload: {} - Headers: {}", topic, payload, headers);
    }
}
