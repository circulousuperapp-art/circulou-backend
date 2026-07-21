package br.com.circulou.circulou_backend.port.out;

import java.util.List;

/**
 * Porta de saída para publicação de eventos de domínio.
 * Implementações devem garantir que os eventos sejam entregues aos interessados.
 * 
 * NOTA DE IDEMPOTÊNCIA: Listeners não devem republicar o mesmo evento recebido.
 */
public interface EventPublisherPort {
    void publish(Object event);
    
    default void publish(List<Object> events) {
        if (events != null) {
            events.forEach(this::publish);
        }
    }
}
