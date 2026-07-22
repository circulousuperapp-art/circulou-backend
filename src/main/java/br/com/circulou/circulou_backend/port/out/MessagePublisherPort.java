package br.com.circulou.circulou_backend.port.out;

import java.util.Map;

/**
 * Porta de saída responsável exclusivamente pelo envio de mensagens para a infraestrutura.
 * Abstrai o destino final (Kafka, Spring Events, etc) do motor de processamento do Outbox.
 */
public interface MessagePublisherPort {
    void publish(String topic, String payload, Map<String, String> headers);
}
