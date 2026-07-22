package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.model.event.*;
import br.com.circulou.circulou_backend.port.out.EventPublisherPort;
import br.com.circulou.circulou_backend.port.out.MessagePublisherPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class KafkaEventPublisherAdapter implements EventPublisherPort, MessagePublisherPort {

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisherAdapter.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(Object event) {
        String topic = determineTopic(event);
        String correlationId = UUID.randomUUID().toString();
        String payload;

        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            logger.error("Erro ao serializar evento para Kafka: {}", event.getClass().getSimpleName(), e);
            return;
        }

        publish(topic, payload, Map.of("correlation-id", correlationId));
    }

    @Override
    public void publish(String topic, String payload, Map<String, String> headers) {
        logger.debug("[KAFKA INFRA] Publicando mensagem no tópico {}: {}", topic, topic);

        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, payload);
        
        if (headers != null) {
            headers.forEach((key, value) -> 
                record.headers().add(new RecordHeader(key, value.getBytes(StandardCharsets.UTF_8))));
        }

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
