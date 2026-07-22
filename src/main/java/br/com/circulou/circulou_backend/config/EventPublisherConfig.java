package br.com.circulou.circulou_backend.config;

import br.com.circulou.circulou_backend.adapter.out.event.KafkaEventPublisherAdapter;
import br.com.circulou.circulou_backend.adapter.out.event.SpringEventPublisherAdapter;
import br.com.circulou.circulou_backend.adapter.out.logging.LoggingEventPublisherAdapter;
import br.com.circulou.circulou_backend.port.out.EventPublisherPort;
import br.com.circulou.circulou_backend.port.out.MessagePublisherPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class EventPublisherConfig {

    // --- MessagePublisherPort Beans (Infrastructure Side / Outbox Relayer) ---

    @Bean
    @ConditionalOnProperty(name = "circulou.message.publisher.type", havingValue = "kafka")
    public MessagePublisherPort kafkaMessagePublisher(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        return new KafkaEventPublisherAdapter(kafkaTemplate, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "circulou.message.publisher.type", havingValue = "spring")
    public MessagePublisherPort springMessagePublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new SpringEventPublisherAdapter(applicationEventPublisher);
    }

    @Bean
    @ConditionalOnProperty(name = "circulou.message.publisher.type", havingValue = "logging")
    public MessagePublisherPort loggingMessagePublisher() {
        return new LoggingEventPublisherAdapter();
    }

    // --- EventPublisherPort Beans (Domain Side / Application Services) ---

    @Bean
    @ConditionalOnProperty(name = "circulou.event.publisher.type", havingValue = "kafka")
    public EventPublisherPort kafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        return new KafkaEventPublisherAdapter(kafkaTemplate, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "circulou.event.publisher.type", havingValue = "spring")
    public EventPublisherPort springEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new SpringEventPublisherAdapter(applicationEventPublisher);
    }

    @Bean
    @ConditionalOnProperty(name = "circulou.event.publisher.type", havingValue = "logging")
    public EventPublisherPort loggingEventPublisher() {
        return new LoggingEventPublisherAdapter();
    }
}
