package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.model.event.PedidoCriadoEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JacksonEventSerializerTest {

    private JacksonEventSerializerAdapter serializer;
    private ObjectMapper objectMapper;
    private EventCatalog eventCatalog;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        eventCatalog = new EventCatalog();
        serializer = new JacksonEventSerializerAdapter(objectMapper, eventCatalog);
    }

    @Test
    void deveSerializarEventoComEnvelopeEMetadados() throws Exception {
        PedidoCriadoEvent event = new PedidoCriadoEvent(1L);

        String json = serializer.serialize(event);

        assertNotNull(json);
        JsonNode root = objectMapper.readTree(json);

        assertTrue(root.has("metadata"));
        assertTrue(root.has("payload"));

        JsonNode metadata = root.get("metadata");
        assertEquals("PedidoCriadoEvent", metadata.get("eventType").asText());
        assertEquals(1, metadata.get("eventVersion").asInt());
        assertNotNull(metadata.get("eventId").asText());
        assertNotNull(metadata.get("occurredAt").asText());
        assertEquals("circulou-backend", metadata.get("source").asText());

        JsonNode payload = root.get("payload");
        assertEquals(1L, payload.get("pedidoId").asLong());
    }
}
