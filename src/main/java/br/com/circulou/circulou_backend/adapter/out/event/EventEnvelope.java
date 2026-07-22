package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.shared.event.EventMetadata;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventEnvelope<T> {
    private final EventMetadata metadata;
    private final T payload;

    public EventEnvelope(EventMetadata metadata, T payload) {
        this.metadata = metadata;
        this.payload = payload;
    }
}
