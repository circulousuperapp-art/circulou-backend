package br.com.circulou.circulou_backend.repository;

import br.com.circulou.circulou_backend.model.OutboxEvent;
import br.com.circulou.circulou_backend.model.OutboxStatus;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaOutboxRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByStatus(OutboxStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")})
    @Query("SELECT e FROM OutboxEvent e WHERE e.status IN (br.com.circulou.circulou_backend.model.OutboxStatus.PENDENTE, br.com.circulou.circulou_backend.model.OutboxStatus.FALHA) AND (e.nextAttemptAt IS NULL OR e.nextAttemptAt <= :now) ORDER BY e.createdAt ASC, e.id ASC")
    List<OutboxEvent> findPendingWithLock(LocalDateTime now, Pageable pageable);
    
    long countByStatus(OutboxStatus status);
}
