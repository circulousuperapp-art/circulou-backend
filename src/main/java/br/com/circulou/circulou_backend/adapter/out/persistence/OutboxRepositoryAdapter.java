package br.com.circulou.circulou_backend.adapter.out.persistence;

import br.com.circulou.circulou_backend.model.OutboxEvent;
import br.com.circulou.circulou_backend.model.OutboxStatus;
import br.com.circulou.circulou_backend.port.out.OutboxRepositoryPort;
import br.com.circulou.circulou_backend.repository.JpaOutboxRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class OutboxRepositoryAdapter implements OutboxRepositoryPort {

    private final JpaOutboxRepository repository;

    public OutboxRepositoryAdapter(JpaOutboxRepository repository) {
        this.repository = repository;
    }

    @Override
    public OutboxEvent save(OutboxEvent outboxEvent) {
        return repository.save(outboxEvent);
    }

    @Override
    public Optional<OutboxEvent> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<OutboxEvent> findByStatus(OutboxStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public List<OutboxEvent> findPendingToProcess(LocalDateTime now, int limit) {
        return repository.findPendingWithLock(now, PageRequest.of(0, limit));
    }

    @Override
    public long countByStatus(OutboxStatus status) {
        return repository.countByStatus(status);
    }
}
