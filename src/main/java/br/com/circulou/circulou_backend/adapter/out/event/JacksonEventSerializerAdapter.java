package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.model.event.DomainEvent;
import br.com.circulou.circulou_backend.port.out.EventSerializerPort;
import br.com.circulou.circulou_backend.shared.event.EventMetadata;
import br.com.circulou.circulou_backend.shared.observability.CorrelationContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class JacksonEventSerializerAdapter implements EventSerializerPort {

    private static final Logger logger = LoggerFactory.getLogger(JacksonEventSerializerAdapter.class);
    private final ObjectMapper objectMapper;
    private final EventCatalog eventCatalog;

    public JacksonEventSerializerAdapter(ObjectMapper objectMapper, EventCatalog eventCatalog) {
        this.objectMapper = objectMapper;
        this.eventCatalog = eventCatalog;
    }

    @Override
    public String serialize(DomainEvent event) {
        String correlationId = CorrelationContext.getAsString();
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        int version = eventCatalog.getInfo(event.getClass())
                .map(EventCatalog.EventInfo::version)
                .orElse(1);

        EventMetadata metadata = new EventMetadata(
            UUID.randomUUID().toString(),
            event.getClass().getSimpleName(),
            version,
            LocalDateTime.now(),
            correlationId,
            "circulou-backend"
        );

        EventEnvelope<DomainEvent> envelope = new EventEnvelope<>(metadata, event);

        try {
            return objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException e) {
            logger.error("Erro ao serializar evento: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Falha na serialização do evento", e);
        }
    }
}
