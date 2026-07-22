package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.integration.BaseIntegrationTest;
import br.com.circulou.circulou_backend.model.OutboxEvent;
import br.com.circulou.circulou_backend.model.OutboxStatus;
import br.com.circulou.circulou_backend.model.event.PedidoCriadoEvent;
import br.com.circulou.circulou_backend.port.out.EventPublisherPort;
import br.com.circulou.circulou_backend.port.out.OutboxRepositoryPort;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class OutboxContractIT extends BaseIntegrationTest {

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("outboxEventPublisherAdapter")
    private EventPublisherPort eventPublisher;

    @Autowired
    private OutboxRepositoryPort outboxRepository;

    @Test
    void devePersistirEventoComContratoPadronizadoETopicoCorreto() throws Exception {
        // Given
        PedidoCriadoEvent event = new PedidoCriadoEvent(123L);

        // When
        eventPublisher.publish(event);

        // Then
        List<OutboxEvent> events = outboxRepository.findByStatus(OutboxStatus.PENDENTE);
        assertFalse(events.isEmpty());
        
        OutboxEvent outboxEvent = events.stream()
                .filter(e -> "PedidoCriadoEvent".equals(e.getEventType()) && e.getAggregateId().equals("123"))
                .findFirst()
                .orElseThrow();

        assertEquals("PedidoCriadoEvent", outboxEvent.getEventType());
        assertEquals(KafkaTopics.PEDIDO_CRIADO, outboxEvent.getTopic());
        assertNotNull(outboxEvent.getPayload());

        // Validar Estrutura JSON (Envelope)
        JsonNode root = objectMapper.readTree(outboxEvent.getPayload());
        assertTrue(root.has("metadata"));
        assertTrue(root.has("payload"));

        JsonNode metadata = root.get("metadata");
        assertEquals("PedidoCriadoEvent", metadata.get("eventType").asText());
        assertEquals(1, metadata.get("eventVersion").asInt());
        assertNotNull(metadata.get("eventId").asText());
        assertNotNull(metadata.get("occurredAt").asText());
        assertNotNull(metadata.get("correlationId").asText());

        JsonNode payload = root.get("payload");
        assertEquals(123L, payload.get("pedidoId").asLong());
    }
}
