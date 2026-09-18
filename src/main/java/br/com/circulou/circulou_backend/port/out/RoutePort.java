package br.com.circulou.circulou_backend.port.out;

public interface RoutePort {

    RouteData calcularRota(
        GeoCoordinate origem,
        GeoCoordinate destino
    );
}
