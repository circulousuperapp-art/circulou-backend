package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.model.event.*;
import br.com.circulou.circulou_backend.port.out.TopicRegistryPort;
import org.springframework.stereotype.Component;

@Component
public class DefaultTopicRegistryAdapter implements TopicRegistryPort {

    private final EventCatalog eventCatalog;

    public DefaultTopicRegistryAdapter(EventCatalog eventCatalog) {
        this.eventCatalog = eventCatalog;
    }

    @Override
    public String resolveTopic(DomainEvent event) {
        return eventCatalog.getInfo(event.getClass())
                .map(EventCatalog.EventInfo::topic)
                .orElseGet(() -> "circulou.events." + event.getClass().getSimpleName()
                        .replaceAll("([a-z])([A-Z])", "$1-$2")
                        .toLowerCase());
    }
}
