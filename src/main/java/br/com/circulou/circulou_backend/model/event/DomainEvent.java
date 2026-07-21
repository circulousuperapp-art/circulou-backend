package br.com.circulou.circulou_backend.model.event;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime getTimestamp();
}
