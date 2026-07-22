package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.shared.event.EventMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEnvelope<T> {
    private EventMetadata metadata;
    private T payload;
}
