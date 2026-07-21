package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.port.out.EventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "circulou.event.publisher.type", havingValue = "spring", matchIfMissing = true)
public class SpringEventPublisherAdapter implements EventPublisherPort {

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
}
