package br.com.circulou.circulou_backend.adapter.out.logging;

import br.com.circulou.circulou_backend.port.out.EventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingEventPublisherAdapter implements EventPublisherPort {

    private static final Logger logger = LoggerFactory.getLogger(LoggingEventPublisherAdapter.class);

    @Override
    public void publish(Object event) {
        logger.info("[DOMAIN EVENT] Publicando: {} - Conteúdo: {}", event.getClass().getSimpleName(), event);
    }
}
