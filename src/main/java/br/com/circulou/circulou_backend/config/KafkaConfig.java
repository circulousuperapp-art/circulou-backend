package br.com.circulou.circulou_backend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
@ConditionalOnProperty(name = "circulou.event.publisher.type", havingValue = "kafka")
public class KafkaConfig {
    // Classe de configuração para habilitar o suporte ao Apache Kafka no projeto.
    // Futuras configurações de tópicos e partições podem ser centralizadas aqui.
}
