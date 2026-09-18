package br.com.circulou.circulou_backend.adapter.out.maps;

import br.com.circulou.circulou_backend.port.out.GeoCoordinate;
import br.com.circulou.circulou_backend.port.out.RouteData;
import br.com.circulou.circulou_backend.port.out.RoutePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GoogleRoutesAdapter implements RoutePort {

    private static final Logger logger = LoggerFactory.getLogger(GoogleRoutesAdapter.class);

    private final RestClient restClient;
    private final String apiKey;

    public GoogleRoutesAdapter(
            RestClient googleRoutesRestClient,
            @Value("${circulou.google.maps.api-key}") String apiKey) {
        this.restClient = googleRoutesRestClient;
        this.apiKey = apiKey;
    }

    @Override
    public RouteData calcularRota(GeoCoordinate origem, GeoCoordinate destino) {
        GoogleRoutesRequest requestBody = createRequest(origem, destino);

        try {
            GoogleRoutesResponse response = restClient.post()
                    .uri("/directions/v2:computeRoutes")
                    .header("X-Goog-Api-Key", apiKey)
                    .header("X-Goog-FieldMask", "routes.distanceMeters,routes.duration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        logger.error("[GOOGLE ROUTES] Erro na API Google: Status {}", res.getStatusCode());
                        throw new RouteCalculationException("Falha na chamada à Google Routes API: " + res.getStatusCode());
                    })
                    .body(GoogleRoutesResponse.class);

            return mapToRouteData(response);
        } catch (Exception e) {
            logger.error("[GOOGLE ROUTES] Falha crítica ao calcular rota: {}", e.getMessage());
            if (e instanceof RouteCalculationException) {
                throw (RouteCalculationException) e;
            }
            throw new RouteCalculationException("Erro ao processar cálculo de rota no Google", e);
        }
    }

    private GoogleRoutesRequest createRequest(GeoCoordinate origem, GeoCoordinate destino) {
        return new GoogleRoutesRequest(
                new GoogleRoutesRequest.Waypoint(new GoogleRoutesRequest.Location(
                        new GoogleRoutesRequest.LatLng(origem.latitude(), origem.longitude()))),
                new GoogleRoutesRequest.Waypoint(new GoogleRoutesRequest.Location(
                        new GoogleRoutesRequest.LatLng(destino.latitude(), destino.longitude()))),
                "DRIVE",
                "TRAFFIC_UNAWARE"
        );
    }

    private RouteData mapToRouteData(GoogleRoutesResponse response) {
        if (response == null || response.routes() == null || response.routes().isEmpty()) {
            throw new RouteCalculationException("Nenhuma rota encontrada ou resposta inválida do Google");
        }

        GoogleRoutesResponse.Route route = response.routes().get(0);

        if (route.distanceMeters() == null || route.duration() == null) {
            throw new RouteCalculationException("Dados de distância ou duração ausentes na resposta do Google");
        }

        return new RouteData(
                route.distanceMeters(),
                parseDuration(route.duration())
        );
    }

    private Integer parseDuration(String durationStr) {
        try {
            // Google retorna "742s", removemos o 's' final
            if (durationStr.endsWith("s")) {
                return Integer.parseInt(durationStr.substring(0, durationStr.length() - 1));
            }
            return Integer.parseInt(durationStr);
        } catch (NumberFormatException e) {
            throw new RouteCalculationException("Formato de duração inválido retornado pelo Google: " + durationStr);
        }
    }
}
