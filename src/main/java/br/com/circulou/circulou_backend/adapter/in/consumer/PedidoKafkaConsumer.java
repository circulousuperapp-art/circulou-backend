package br.com.circulou.circulou_backend.adapter.in.consumer;

import br.com.circulou.circulou_backend.adapter.out.event.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PedidoKafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PedidoKafkaConsumer.class);

    @KafkaListener(topics = KafkaTopics.PEDIDO_CRIADO, groupId = "${spring.kafka.consumer.group-id}")
    public void consumePedidoCriado(@Payload Object event, @Header("correlation-id") String correlationId) {
        logger.info("[KAFKA CONSUMER] Mensagem recebida no tópico {}. Payload: {} (CorrelationId: {})", 
                KafkaTopics.PEDIDO_CRIADO, event, correlationId);
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_CANCELADO, groupId = "${spring.kafka.consumer.group-id}")
    public void consumePedidoCancelado(@Payload Object event, @Header("correlation-id") String correlationId) {
        logger.info("[KAFKA CONSUMER] Mensagem recebida no tópico {}. Payload: {} (CorrelationId: {})", 
                KafkaTopics.PEDIDO_CANCELADO, event, correlationId);
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_LIBERADO, groupId = "${spring.kafka.consumer.group-id}")
    public void consumePedidoLiberado(@Payload Object event, @Header("correlation-id") String correlationId) {
        logger.info("[KAFKA CONSUMER] Mensagem recebida no tópico {}. Payload: {} (CorrelationId: {})", 
                KafkaTopics.PEDIDO_LIBERADO, event, correlationId);
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_ENTREGUE, groupId = "${spring.kafka.consumer.group-id}")
    public void consumePedidoEntregue(@Payload Object event, @Header("correlation-id") String correlationId) {
        logger.info("[KAFKA CONSUMER] Mensagem recebida no tópico {}. Payload: {} (CorrelationId: {})", 
                KafkaTopics.PEDIDO_ENTREGUE, event, correlationId);
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_EM_ROTA, groupId = "${spring.kafka.consumer.group-id}")
    public void consumePedidoEmRota(@Payload Object event, @Header("correlation-id") String correlationId) {
        logger.info("[KAFKA CONSUMER] Mensagem recebida no tópico {}. Payload: {} (CorrelationId: {})", 
                KafkaTopics.PEDIDO_EM_ROTA, event, correlationId);
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_PRONTO_PARA_RETIRADA, groupId = "${spring.kafka.consumer.group-id}")
    public void consumePedidoProntoParaRetirada(@Payload Object event, @Header("correlation-id") String correlationId) {
        logger.info("[KAFKA CONSUMER] Mensagem recebida no tópico {}. Payload: {} (CorrelationId: {})", 
                KafkaTopics.PEDIDO_PRONTO_PARA_RETIRADA, event, correlationId);
    }

    @KafkaListener(topics = KafkaTopics.PEDIDO_EM_PREPARO, groupId = "${spring.kafka.consumer.group-id}")
    public void consumePedidoEmPreparo(@Payload Object event, @Header("correlation-id") String correlationId) {
        logger.info("[KAFKA CONSUMER] Mensagem recebida no tópico {}. Payload: {} (CorrelationId: {})", 
                KafkaTopics.PEDIDO_EM_PREPARO, event, correlationId);
    }
}
