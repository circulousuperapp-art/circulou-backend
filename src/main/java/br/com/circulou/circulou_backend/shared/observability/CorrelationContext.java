package br.com.circulou.circulou_backend.shared.observability;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.MDC;

public class CorrelationContext {

    private static final String MDC_KEY = "correlationId";
    private static final ThreadLocal<CorrelationId> context = new ThreadLocal<>();

    public static void set(CorrelationId correlationId, ObservationRegistry observationRegistry) {
        context.set(correlationId);
        if (correlationId != null) {
            MDC.put(MDC_KEY, correlationId.value());
            
            // Integrando com Micrometer Observation se disponível
            if (observationRegistry != null) {
                Observation currentObservation = observationRegistry.getCurrentObservation();
                if (currentObservation != null) {
                    currentObservation.highCardinalityKeyValue(MDC_KEY, correlationId.value());
                }
            }
        } else {
            MDC.remove(MDC_KEY);
        }
    }

    public static void set(CorrelationId correlationId) {
        set(correlationId, null);
    }

    public static CorrelationId get() {
        return context.get();
    }

    public static String getAsString() {
        CorrelationId id = context.get();
        return id != null ? id.value() : null;
    }

    public static void clear() {
        context.remove();
        MDC.remove(MDC_KEY);
    }
}