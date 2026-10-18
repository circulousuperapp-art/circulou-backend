package br.com.circulou.circulou_backend.adapter.out.maps;

import br.com.circulou.circulou_backend.port.out.GeoCoordinate;
import br.com.circulou.circulou_backend.port.out.RouteData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class GoogleRoutesAdapterTest {

    private GoogleRoutesAdapter adapter;
    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.baseUrl("https://routes.googleapis.com").build();
        adapter = new GoogleRoutesAdapter(restClient, "test-api-key");
    }

    @Test
    @DisplayName("Deve calcular rota com sucesso")
    void deveCalcularRotaComSucesso() throws Exception {
        // GIVEN
        GeoCoordinate origem = new GeoCoordinate(-23.5505, -46.6333);
        GeoCoordinate destino = new GeoCoordinate(-23.5596, -46.6583);

        GoogleRoutesResponse response = new GoogleRoutesResponse(
                List.of(new GoogleRoutesResponse.Route(5234, "742s", new GoogleRoutesResponse.Polyline("encoded-polyline-test")))
        );

        mockServer.expect(requestTo("https://routes.googleapis.com/directions/v2:computeRoutes"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Goog-Api-Key", "test-api-key"))
                .andExpect(header("X-Goog-FieldMask", "routes.distanceMeters,routes.duration,routes.polyline.encodedPolyline"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.origin.location.latLng.latitude").value(-23.5505))
                .andExpect(jsonPath("$.origin.location.latLng.longitude").value(-46.6333))
                .andExpect(jsonPath("$.destination.location.latLng.latitude").value(-23.5596))
                .andExpect(jsonPath("$.destination.location.latLng.longitude").value(-46.6583))
                .andExpect(jsonPath("$.travelMode").value("DRIVE"))
                .andExpect(jsonPath("$.routingPreference").value("TRAFFIC_UNAWARE"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        // WHEN
        RouteData result = adapter.calcularRota(origem, destino);

        // THEN
        assertThat(result.distanciaMetros()).isEqualTo(5234);
        assertThat(result.duracaoSegundos()).isEqualTo(742);
        assertThat(result.encodedPolyline()).isEqualTo("encoded-polyline-test");
        mockServer.verify();
    }

    @Test
    @DisplayName("Deve calcular rota com sucesso quando polyline está ausente")
    void deveCalcularRotaComSucessoSemPolyline() throws Exception {
        // GIVEN
        GeoCoordinate origem = new GeoCoordinate(-23.5505, -46.6333);
        GeoCoordinate destino = new GeoCoordinate(-23.5596, -46.6583);

        GoogleRoutesResponse response = new GoogleRoutesResponse(
                List.of(new GoogleRoutesResponse.Route(5234, "742s", null))
        );

        mockServer.expect(requestTo("https://routes.googleapis.com/directions/v2:computeRoutes"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        // WHEN
        RouteData result = adapter.calcularRota(origem, destino);

        // THEN
        assertThat(result.distanciaMetros()).isEqualTo(5234);
        assertThat(result.duracaoSegundos()).isEqualTo(742);
        assertThat(result.encodedPolyline()).isNull();
    }

    @Test
    @DisplayName("Deve falhar quando Google retorna erro 400")
    void deveFalharQuandoGoogleRetornaErro400() {
        // GIVEN
        GeoCoordinate origem = new GeoCoordinate(0.0, 0.0);
        GeoCoordinate destino = new GeoCoordinate(0.0, 0.0);

        mockServer.expect(anything())
                .andRespond(withBadRequest());

        // WHEN & THEN
        assertThatThrownBy(() -> adapter.calcularRota(origem, destino))
                .isInstanceOf(RouteCalculationException.class)
                .hasMessageContaining("Falha na chamada à Google Routes API: 400");
    }

    @Test
    @DisplayName("Deve falhar quando resposta do Google é vazia")
    void deveFalharQuandoRespostaGoogleVazia() throws Exception {
        // GIVEN
        GeoCoordinate origem = new GeoCoordinate(-23.5505, -46.6333);
        GeoCoordinate destino = new GeoCoordinate(-23.5596, -46.6583);

        GoogleRoutesResponse response = new GoogleRoutesResponse(List.of());

        mockServer.expect(anything())
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        // WHEN & THEN
        assertThatThrownBy(() -> adapter.calcularRota(origem, destino))
                .isInstanceOf(RouteCalculationException.class)
                .hasMessageContaining("Nenhuma rota encontrada");
    }

    @Test
    @DisplayName("Deve falhar quando duração tem formato inválido")
    void deveFalharQuandoDuracaoInvalida() throws Exception {
        // GIVEN
        GeoCoordinate origem = new GeoCoordinate(-23.5505, -46.6333);
        GeoCoordinate destino = new GeoCoordinate(-23.5596, -46.6583);

        GoogleRoutesResponse response = new GoogleRoutesResponse(
                List.of(new GoogleRoutesResponse.Route(1000, "invalid", null))
        );

        mockServer.expect(anything())
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        // WHEN & THEN
        assertThatThrownBy(() -> adapter.calcularRota(origem, destino))
                .isInstanceOf(RouteCalculationException.class)
                .hasMessageContaining("Formato de duração inválido");
    }

    @Test
    @DisplayName("Deve falhar quando resposta do Google é nula")
    void deveFalharQuandoRespostaGoogleNula() {
        // GIVEN
        GeoCoordinate origem = new GeoCoordinate(-23.5505, -46.6333);
        GeoCoordinate destino = new GeoCoordinate(-23.5596, -46.6583);

        mockServer.expect(anything())
                .andRespond(withSuccess("null", MediaType.APPLICATION_JSON));

        // WHEN & THEN
        assertThatThrownBy(() -> adapter.calcularRota(origem, destino))
                .isInstanceOf(RouteCalculationException.class)
                .hasMessageContaining("Nenhuma rota encontrada");
    }

    @Test
    @DisplayName("Deve falhar quando Google retorna erro 500")
    void deveFalharQuandoGoogleRetornaErro500() {
        // GIVEN
        GeoCoordinate origem = new GeoCoordinate(0.0, 0.0);
        GeoCoordinate destino = new GeoCoordinate(0.0, 0.0);

        mockServer.expect(anything())
                .andRespond(withServerError());

        // WHEN & THEN
        assertThatThrownBy(() -> adapter.calcularRota(origem, destino))
                .isInstanceOf(RouteCalculationException.class)
                .hasMessageContaining("Falha na chamada à Google Routes API: 500");
    }

    @Test
    @DisplayName("Deve falhar quando o Google retorna 429 Too Many Requests")
    void deveFalharQuandoGoogleRetorna429() {
        // GIVEN
        GeoCoordinate origem = new GeoCoordinate(0.0, 0.0);
        GeoCoordinate destino = new GeoCoordinate(0.0, 0.0);

        mockServer.expect(anything())
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        // WHEN & THEN
        assertThatThrownBy(() -> adapter.calcularRota(origem, destino))
                .isInstanceOf(RouteCalculationException.class)
                .hasMessageContaining("Falha na chamada à Google Routes API: 429 TOO_MANY_REQUESTS");
    }

    @Test
    @DisplayName("Deve falhar quando campos essenciais estão ausentes")
    void deveFalharQuandoCamposAusentes() throws Exception {
        // GIVEN
        GeoCoordinate origem = new GeoCoordinate(-23.5505, -46.6333);
        GeoCoordinate destino = new GeoCoordinate(-23.5596, -46.6583);

        // distanceMeters ausente
        GoogleRoutesResponse response = new GoogleRoutesResponse(
                List.of(new GoogleRoutesResponse.Route(null, "742s", null))
        );

        mockServer.expect(anything())
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        // WHEN & THEN
        assertThatThrownBy(() -> adapter.calcularRota(origem, destino))
                .isInstanceOf(RouteCalculationException.class)
                .hasMessageContaining("Dados de distância ou duração ausentes");
    }
}
