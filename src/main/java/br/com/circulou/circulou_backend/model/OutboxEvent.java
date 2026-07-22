package br.com.circulou.circulou_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event", indexes = {
    @Index(name = "idx_outbox_polling", columnList = "status, nextAttemptAt, createdAt, id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OutboxStatus status;

    @Builder.Default
    @Column(nullable = false)
    private Integer attemptCount = 0;

    @Column(columnDefinition = "TEXT")
    private String lastError;

    @Builder.Default
    @Column(nullable = false)
    private Integer version = 1;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime processedAt;

    @Column
    private LocalDateTime nextAttemptAt;
}
