package br.com.circulou.circulou_backend.port.out;

import br.com.circulou.circulou_backend.model.event.DomainEvent;

public interface TopicRegistryPort {
    String resolveTopic(DomainEvent event);
}
