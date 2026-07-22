package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.model.OutboxEvent;
import br.com.circulou.circulou_backend.model.OutboxStatus;
import br.com.circulou.circulou_backend.model.event.*;
import br.com.circulou.circulou_backend.port.out.EventPublisherPort;
import br.com.circulou.circulou_backend.port.out.EventSerializerPort;
import br.com.circulou.circulou_backend.port.out.OutboxRepositoryPort;
import br.com.circulou.circulou_backend.port.out.TopicRegistryPort;
import br.com.circulou.circulou_backend.shared.observability.CorrelationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "circulou.event.publisher.type", havingValue = "outbox")
public class OutboxEventPublisherAdapter implements EventPublisherPort {

    private static final Logger logger = LoggerFactory.getLogger(OutboxEventPublisherAdapter.class);
    private final OutboxRepositoryPort outboxRepositoryPort;
    private final EventSerializerPort eventSerializer;
    private final TopicRegistryPort topicRegistry;

    public OutboxEventPublisherAdapter(OutboxRepositoryPort outboxRepositoryPort, 
                                       EventSerializerPort eventSerializer,
                                       TopicRegistryPort topicRegistry) {
        this.outboxRepositoryPort = outboxRepositoryPort;
        this.eventSerializer = eventSerializer;
        this.topicRegistry = topicRegistry;
    }

    @Override
    public void publish(Object event) {
        if (!(event instanceof DomainEvent domainEvent)) {
            logger.warn("Evento recebido não é um DomainEvent: {}", event.getClass().getSimpleName());
            return;
        }

        String correlationId = CorrelationContext.getAsString();
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        try {
            String payload = eventSerializer.serialize(domainEvent);
            String topic = topicRegistry.resolveTopic(domainEvent);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(extractAggregateId(domainEvent))
                    .aggregateType(extractAggregateType(domainEvent))
                    .eventType(domainEvent.getClass().getSimpleName())
                    .topic(topic)
                    .payload(payload)
                    .correlationId(correlationId)
                    .status(OutboxStatus.PENDENTE)
                    .attemptCount(0)
                    .version(1)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxRepositoryPort.save(outboxEvent);
            logger.debug("[OUTBOX EVENT] Evento persistido (CorrelationId: {}): {} para agregado {} no tópico {}", 
                    correlationId, outboxEvent.getEventType(), outboxEvent.getAggregateId(), topic);

        } catch (Exception e) {
            logger.error("Erro ao processar evento para outbox: {}", domainEvent.getClass().getSimpleName(), e);
        }
    }

    private String extractAggregateId(DomainEvent event) {
        if (event instanceof PedidoCriadoEvent e) return e.pedidoId().toString();
        if (event instanceof PedidoCanceladoEvent e) return e.pedidoId().toString();
        if (event instanceof PedidoEmPreparoEvent e) return e.pedidoId().toString();
        if (event instanceof PedidoEmRotaEvent e) return e.pedidoId().toString();
        if (event instanceof PedidoEntregueEvent e) return e.pedidoId().toString();
        if (event instanceof PedidoLiberadoEvent e) return e.pedidoId().toString();
        if (event instanceof PedidoProntoParaRetiradaEvent e) return e.pedidoId().toString();
        
        return "unknown";
    }

    private String extractAggregateType(DomainEvent event) {
        if (event instanceof PedidoCriadoEvent || 
            event instanceof PedidoCanceladoEvent || 
            event instanceof PedidoEmPreparoEvent || 
            event instanceof PedidoEmRotaEvent || 
            event instanceof PedidoEntregueEvent || 
            event instanceof PedidoLiberadoEvent || 
            event instanceof PedidoProntoParaRetiradaEvent) {
            return "Pedido";
        }
        
        return "Unknown";
    }
}
