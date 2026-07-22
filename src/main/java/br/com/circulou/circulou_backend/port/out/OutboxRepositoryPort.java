package br.com.circulou.circulou_backend.port.out;

import br.com.circulou.circulou_backend.model.OutboxEvent;
import br.com.circulou.circulou_backend.model.OutboxStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OutboxRepositoryPort {
    OutboxEvent save(OutboxEvent outboxEvent);
    Optional<OutboxEvent> findById(Long id);
    List<OutboxEvent> findByStatus(OutboxStatus status);
    List<OutboxEvent> findPendingToProcess(LocalDateTime now, int limit);
    long countByStatus(OutboxStatus status);
}
