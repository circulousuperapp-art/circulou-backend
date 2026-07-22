package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.model.OutboxEvent;
import br.com.circulou.circulou_backend.model.OutboxStatus;
import br.com.circulou.circulou_backend.port.out.MessagePublisherPort;
import br.com.circulou.circulou_backend.port.out.OutboxRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "circulou.outbox.enabled", havingValue = "true")
public class OutboxRelayer {

    private static final Logger logger = LoggerFactory.getLogger(OutboxRelayer.class);

    private final OutboxRepositoryPort outboxRepositoryPort;
    private final MessagePublisherPort messagePublisherPort;
    private final MeterRegistry meterRegistry;

    @Value("${circulou.outbox.batch-size:100}")
    private int batchSize;

    @Value("${circulou.outbox.max-attempts:5}")
    private int maxAttempts;

    @Value("${circulou.outbox.retry-interval-ms:1000}")
    private long retryIntervalMs;

    public OutboxRelayer(OutboxRepositoryPort outboxRepositoryPort, 
                         MessagePublisherPort messagePublisherPort,
                         MeterRegistry meterRegistry) {
        this.outboxRepositoryPort = outboxRepositoryPort;
        this.messagePublisherPort = messagePublisherPort;
        this.meterRegistry = meterRegistry;
        registerGauges();
    }

    private void registerGauges() {
        meterRegistry.gauge("circulou.outbox.backlog.pending", 
                outboxRepositoryPort, 
                repo -> repo.countByStatus(OutboxStatus.PENDENTE));
        
        meterRegistry.gauge("circulou.outbox.backlog.failed", 
                outboxRepositoryPort, 
                repo -> repo.countByStatus(OutboxStatus.FALHA));
    }

    @Scheduled(fixedDelayString = "${circulou.outbox.interval-ms:5000}")
    @Transactional
    public void processOutbox() {
        List<OutboxEvent> events = outboxRepositoryPort.findPendingToProcess(LocalDateTime.now(), batchSize);

        if (events.isEmpty()) {
            return;
        }

        logger.debug("[OUTBOX RELAYER] Iniciando processamento de {} eventos com lock", events.size());

        for (OutboxEvent event : events) {
            try {
                publishEvent(event);
            } catch (Exception e) {
                logger.error("[OUTBOX RELAYER] Erro ao processar evento {}: {}", event.getId(), e.getMessage());
                handleFailure(event, e);
            }
        }
    }

    private void publishEvent(OutboxEvent event) {
        String topic = determineTopic(event.getEventType());
        Map<String, String> headers = Map.of("correlation-id", event.getCorrelationId());

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            messagePublisherPort.publish(topic, event.getPayload(), headers);
            sample.stop(Timer.builder("circulou.outbox.publish.time")
                    .tag("event_type", event.getEventType())
                    .register(meterRegistry));
        } catch (Exception e) {
            // Se falhar a publicação, a exceção será capturada pelo loop no processOutbox
            throw e;
        }

        LocalDateTime now = LocalDateTime.now();

        // Timer de processamento total (criação -> publicação)
        Timer.builder("circulou.outbox.processing.time")
                .tag("event_type", event.getEventType())
                .register(meterRegistry)
                .record(Duration.between(event.getCreatedAt(), now));

        // Contador de sucessos
        meterRegistry.counter("circulou.outbox.events.published", "event_type", event.getEventType()).increment();

        // Distribuição de retries
        meterRegistry.summary("circulou.outbox.retries", "event_type", event.getEventType())
                .record(event.getAttemptCount());

        event.setStatus(OutboxStatus.PROCESSADO);
        event.setProcessedAt(now);
        outboxRepositoryPort.save(event);
    }

    private void handleFailure(OutboxEvent event, Exception e) {
        int currentAttempt = event.getAttemptCount() + 1;
        event.setAttemptCount(currentAttempt);
        event.setLastError(e.getMessage());

        String errorType = e.getClass().getSimpleName();
        boolean isFinal = false;

        if (currentAttempt >= maxAttempts) {
            event.setStatus(OutboxStatus.FALHA_DEFINITIVA);
            isFinal = true;
            logger.error("[OUTBOX RELAYER] Evento {} atingiu o limite de {} tentativas e foi marcado como FALHA_DEFINITIVA", 
                    event.getId(), maxAttempts);
        } else {
            event.setStatus(OutboxStatus.FALHA);
            // Exponential Backoff: now + (2^attempt * interval)
            long delay = (long) (Math.pow(2, currentAttempt) * retryIntervalMs);
            event.setNextAttemptAt(LocalDateTime.now().plusNanos(delay * 1_000_000));
            logger.warn("[OUTBOX RELAYER] Evento {} falhou (tentativa {}/{}). Próxima tentativa em {}ms", 
                    event.getId(), currentAttempt, maxAttempts, delay);
        }

        // Contador de falhas
        meterRegistry.counter("circulou.outbox.events.failed", 
                "event_type", event.getEventType(),
                "error_type", errorType,
                "is_final", String.valueOf(isFinal))
                .increment();

        outboxRepositoryPort.save(event);
    }

    private String determineTopic(String eventType) {
        return switch (eventType) {
            case "PedidoCriadoEvent" -> KafkaTopics.PEDIDO_CRIADO;
            case "PedidoCanceladoEvent" -> KafkaTopics.PEDIDO_CANCELADO;
            case "PedidoLiberadoEvent" -> KafkaTopics.PEDIDO_LIBERADO;
            case "PedidoEntregueEvent" -> KafkaTopics.PEDIDO_ENTREGUE;
            case "PedidoEmRotaEvent" -> KafkaTopics.PEDIDO_EM_ROTA;
            case "PedidoProntoParaRetiradaEvent" -> KafkaTopics.PEDIDO_PRONTO_PARA_RETIRADA;
            case "PedidoEmPreparoEvent" -> KafkaTopics.PEDIDO_EM_PREPARO;
            default -> "circulou.events." + eventType.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
        };
    }
}
