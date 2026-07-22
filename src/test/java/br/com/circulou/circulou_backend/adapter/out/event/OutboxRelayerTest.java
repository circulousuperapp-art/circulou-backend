package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.model.OutboxEvent;
import br.com.circulou.circulou_backend.model.OutboxStatus;
import br.com.circulou.circulou_backend.port.out.MessagePublisherPort;
import br.com.circulou.circulou_backend.port.out.OutboxRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxRelayerTest {

    @Mock
    private OutboxRepositoryPort outboxRepositoryPort;

    @Mock
    private MessagePublisherPort messagePublisherPort;

    private MeterRegistry meterRegistry;
    private OutboxRelayer outboxRelayer;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        outboxRelayer = new OutboxRelayer(outboxRepositoryPort, messagePublisherPort, meterRegistry);
        ReflectionTestUtils.setField(outboxRelayer, "batchSize", 100);
        ReflectionTestUtils.setField(outboxRelayer, "maxAttempts", 5);
        ReflectionTestUtils.setField(outboxRelayer, "retryIntervalMs", 1000L);
    }

    @Test
    void deveRegistrarMetricasDeSucessoNaPublicacao() {
        // Given
        OutboxEvent event = OutboxEvent.builder()
                .id(1L)
                .eventType("PedidoCriadoEvent")
                .payload("{}")
                .correlationId("test-correlation-id")
                .createdAt(LocalDateTime.now().minusMinutes(1))
                .attemptCount(0)
                .status(OutboxStatus.PENDENTE)
                .build();

        when(outboxRepositoryPort.findPendingToProcess(any(), anyInt())).thenReturn(List.of(event));

        // When
        outboxRelayer.processOutbox();

        // Then
        assertEquals(1, meterRegistry.get("circulou.outbox.events.published").counter().count());
        assertEquals(1, meterRegistry.get("circulou.outbox.publish.time").timer().count());
        assertEquals(1, meterRegistry.get("circulou.outbox.processing.time").timer().count());
        assertEquals(0.0, meterRegistry.get("circulou.outbox.retries").summary().max());
        
        verify(messagePublisherPort).publish(eq(KafkaTopics.PEDIDO_CRIADO), eq("{}"), any());
        verify(outboxRepositoryPort).save(argThat(e -> e.getStatus() == OutboxStatus.PROCESSADO));
    }

    @Test
    void deveRegistrarMetricasDeFalhaNaPublicacao() {
        // Given
        OutboxEvent event = OutboxEvent.builder()
                .id(1L)
                .eventType("PedidoCriadoEvent")
                .payload("{}")
                .correlationId("test-correlation-id")
                .createdAt(LocalDateTime.now().minusMinutes(1))
                .attemptCount(0)
                .status(OutboxStatus.PENDENTE)
                .build();

        when(outboxRepositoryPort.findPendingToProcess(any(), anyInt())).thenReturn(List.of(event));
        doThrow(new RuntimeException("Kafka error")).when(messagePublisherPort).publish(any(), any(), any());

        // When
        outboxRelayer.processOutbox();

        // Then
        assertEquals(1, meterRegistry.get("circulou.outbox.events.failed")
                .tag("error_type", "RuntimeException")
                .tag("is_final", "false")
                .counter().count());
        
        verify(outboxRepositoryPort).save(argThat(e -> e.getStatus() == OutboxStatus.FALHA));
    }

    @Test
    void deveRegistrarMetricasDeFalhaDefinitiva() {
        // Given
        OutboxEvent event = OutboxEvent.builder()
                .id(1L)
                .eventType("PedidoCriadoEvent")
                .payload("{}")
                .correlationId("test-correlation-id")
                .createdAt(LocalDateTime.now().minusMinutes(1))
                .attemptCount(4) // Já teve 4 tentativas, a próxima será a 5 (limite)
                .status(OutboxStatus.FALHA)
                .build();

        when(outboxRepositoryPort.findPendingToProcess(any(), anyInt())).thenReturn(List.of(event));
        doThrow(new RuntimeException("Kafka error")).when(messagePublisherPort).publish(any(), any(), any());

        // When
        outboxRelayer.processOutbox();

        // Then
        assertEquals(1, meterRegistry.get("circulou.outbox.events.failed")
                .tag("error_type", "RuntimeException")
                .tag("is_final", "true")
                .counter().count());
        
        verify(outboxRepositoryPort).save(argThat(e -> e.getStatus() == OutboxStatus.FALHA_DEFINITIVA));
    }

    @Test
    void deveRegistrarGaugesDeBacklog() {
        // Given
        when(outboxRepositoryPort.countByStatus(OutboxStatus.PENDENTE)).thenReturn(10L);
        when(outboxRepositoryPort.countByStatus(OutboxStatus.FALHA)).thenReturn(5L);

        // When & Then
        assertEquals(10.0, meterRegistry.get("circulou.outbox.backlog.pending").gauge().value());
        assertEquals(5.0, meterRegistry.get("circulou.outbox.backlog.failed").gauge().value());
    }
}
