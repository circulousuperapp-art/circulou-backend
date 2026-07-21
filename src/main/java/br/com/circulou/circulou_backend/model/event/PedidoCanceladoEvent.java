package br.com.circulou.circulou_backend.model.event;

import java.time.LocalDateTime;

public record PedidoCanceladoEvent(
    Long pedidoId,
    LocalDateTime timestamp
) implements DomainEvent {
    public PedidoCanceladoEvent(Long pedidoId) {
        this(pedidoId, LocalDateTime.now());
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
