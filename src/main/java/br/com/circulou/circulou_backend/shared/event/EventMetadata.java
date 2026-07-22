package br.com.circulou.circulou_backend.shared.event;

import java.time.LocalDateTime;

public record EventMetadata(
    String eventId,
    String eventType,
    Integer eventVersion,
    LocalDateTime occurredAt,
    String correlationId,
    String source
) {}
