package br.com.circulou.circulou_backend.adapter.out.maps;

import br.com.circulou.circulou_backend.exception.BusinessException;

public class RouteCalculationException extends BusinessException {
    public RouteCalculationException(String message) {
        super(message);
    }

    public RouteCalculationException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
