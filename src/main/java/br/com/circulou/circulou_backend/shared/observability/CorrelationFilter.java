package br.com.circulou.circulou_backend.shared.observability;

import br.com.circulou.circulou_backend.shared.observability.CorrelationContext;
import br.com.circulou.circulou_backend.shared.observability.CorrelationId;
import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationFilter implements Filter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private final ObservationRegistry observationRegistry;

    public CorrelationFilter(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest httpRequest) {
            String headerId = httpRequest.getHeader(CORRELATION_ID_HEADER);
            CorrelationId correlationId = new CorrelationId(headerId);
            
            CorrelationContext.set(correlationId, observationRegistry);
            
            if (response instanceof HttpServletResponse httpResponse) {
                httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId.value());
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            CorrelationContext.clear();
        }
    }
}