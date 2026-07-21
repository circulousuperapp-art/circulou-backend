package br.com.circulou.circulou_backend.config;

import br.com.circulou.circulou_backend.adapter.out.event.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
@ConditionalOnProperty(name = "circulou.event.publisher.type", havingValue = "kafka")
public class KafkaConfig {

    @Bean
    public NewTopic pedidoCriadoTopic() {
        return TopicBuilder.name(KafkaTopics.PEDIDO_CRIADO).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic pedidoCanceladoTopic() {
        return TopicBuilder.name(KafkaTopics.PEDIDO_CANCELADO).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic pedidoLiberadoTopic() {
        return TopicBuilder.name(KafkaTopics.PEDIDO_LIBERADO).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic pedidoEntregueTopic() {
        return TopicBuilder.name(KafkaTopics.PEDIDO_ENTREGUE).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic pedidoEmRotaTopic() {
        return TopicBuilder.name(KafkaTopics.PEDIDO_EM_ROTA).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic pedidoProntoParaRetiradaTopic() {
        return TopicBuilder.name(KafkaTopics.PEDIDO_PRONTO_PARA_RETIRADA).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic pedidoEmPreparoTopic() {
        return TopicBuilder.name(KafkaTopics.PEDIDO_EM_PREPARO).partitions(1).replicas(1).build();
    }
}
