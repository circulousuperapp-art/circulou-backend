package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.model.event.PedidoCriadoEvent;
import br.com.circulou.circulou_backend.model.event.PedidoCanceladoEvent;
import br.com.circulou.circulou_backend.model.event.DomainEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultTopicRegistryTest {

    private final EventCatalog eventCatalog = new EventCatalog();
    private final DefaultTopicRegistryAdapter topicRegistry = new DefaultTopicRegistryAdapter(eventCatalog);

    @Test
    void deveResolverTopicoParaPedidoCriado() {
        PedidoCriadoEvent event = new PedidoCriadoEvent(1L);
        assertEquals(KafkaTopics.PEDIDO_CRIADO, topicRegistry.resolveTopic(event));
    }

    @Test
    void deveResolverTopicoParaPedidoCancelado() {
        PedidoCanceladoEvent event = new PedidoCanceladoEvent(1L);
        assertEquals(KafkaTopics.PEDIDO_CANCELADO, topicRegistry.resolveTopic(event));
    }

    @Test
    void deveResolverTopicoFallbackParaEventoDesconhecido() {
        class EventoDesconhecido implements DomainEvent {
            @Override
            public LocalDateTime getTimestamp() {
                return LocalDateTime.now();
            }
        };
        EventoDesconhecido event = new EventoDesconhecido();
        
        assertEquals("circulou.events.evento-desconhecido", topicRegistry.resolveTopic(event));
    }
}
