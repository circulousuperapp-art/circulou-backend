package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.model.event.*;
import br.com.circulou.circulou_backend.port.out.EventPublisherPort;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "circulou.event.publisher.type", havingValue = "kafka")
public class KafkaEventPublisherAdapter implements EventPublisherPort {

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisherAdapter.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(Object event) {
        String topic = determineTopic(event);
        String correlationId = UUID.randomUUID().toString();

        logger.debug("[KAFKA EVENT] Publicando evento no tópico {}: {} (CorrelationId: {})", 
                topic, event.getClass().getSimpleName(), correlationId);

        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, event);
        record.headers().add(new RecordHeader("correlation-id", correlationId.getBytes(StandardCharsets.UTF_8)));

        kafkaTemplate.send(record);
    }

    private String determineTopic(Object event) {
        if (event instanceof PedidoCriadoEvent) return KafkaTopics.PEDIDO_CRIADO;
        if (event instanceof PedidoCanceladoEvent) return KafkaTopics.PEDIDO_CANCELADO;
        if (event instanceof PedidoLiberadoEvent) return KafkaTopics.PEDIDO_LIBERADO;
        if (event instanceof PedidoEntregueEvent) return KafkaTopics.PEDIDO_ENTREGUE;
        if (event instanceof PedidoEmRotaEvent) return KafkaTopics.PEDIDO_EM_ROTA;
        if (event instanceof PedidoProntoParaRetiradaEvent) return KafkaTopics.PEDIDO_PRONTO_PARA_RETIRADA;
        if (event instanceof PedidoEmPreparoEvent) return KafkaTopics.PEDIDO_EM_PREPARO;

        String className = event.getClass().getSimpleName();
        return "circulou.events." + className.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }
}
