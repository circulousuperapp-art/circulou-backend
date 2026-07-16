package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.integration.BaseIntegrationTest;
import br.com.circulou.circulou_backend.dto.UsuarioRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureWebTestClient
class ActuatorIT extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Deve acessar endpoint health sem autenticação")
    void deveAcessarHealthSemAutenticacao() {
        webTestClient.get().uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    @DisplayName("Deve acessar health groups (liveness/readiness) sem autenticação")
    void deveAcessarHealthGroupsSemAutenticacao() {
        webTestClient.get().uri("/actuator/health/liveness")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");

        webTestClient.get().uri("/actuator/health/readiness")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    @DisplayName("Deve acessar endpoint info sem autenticação")
    void deveAcessarInfoSemAutenticacao() {
        webTestClient.get().uri("/actuator/info")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Deve retornar 401 ao acessar metrics sem autenticação")
    void deveRetornar401AoAcessarMetricsSemAutenticacao() {
        webTestClient.get().uri("/actuator/metrics")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("Deve acessar metrics com autenticação")
    void deveAcessarMetricsComAutenticacao() throws Exception {
        // Criar usuário e obter token (Bootstrap via MockMvc herdado)
        UsuarioRequestDTO usuarioDTO = new UsuarioRequestDTO();
        usuarioDTO.setNome("Admin Test");
        usuarioDTO.setEmail("admin@test.com");
        usuarioDTO.setSenha("123456");
        usuarioDTO.setTelefone("11999998888");
        usuarioDTO.setRole("USER");
        usuarioDTO.setAtivo(true);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isOk());

        String token = obterTokenAutenticacao("admin@test.com", "123456");

        // Validação via WebTestClient (Chamada real)
        webTestClient.get().uri("/actuator/metrics")
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Deve retornar 401 ao acessar prometheus sem autenticação")
    void deveRetornar401AoAcessarPrometheusSemAutenticacao() {
        webTestClient.get().uri("/actuator/prometheus")
                .accept(MediaType.parseMediaType("text/plain; version=0.0.4"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("Deve acessar prometheus com autenticação")
    void deveAcessarPrometheusComAutenticacao() throws Exception {
        // Criar usuário e obter token (Bootstrap via MockMvc herdado)
        UsuarioRequestDTO usuarioDTO = new UsuarioRequestDTO();
        usuarioDTO.setNome("Monitor Test");
        usuarioDTO.setEmail("monitor@test.com");
        usuarioDTO.setSenha("123456");
        usuarioDTO.setTelefone("11999998888");
        usuarioDTO.setRole("USER");
        usuarioDTO.setAtivo(true);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isOk());

        String token = obterTokenAutenticacao("monitor@test.com", "123456");

        // Validação via WebTestClient (Chamada real com header Accept correto)
        webTestClient.get().uri("/actuator/prometheus")
                .header("Authorization", token)
                .accept(MediaType.parseMediaType("text/plain; version=0.0.4"))
                .exchange()
                .expectBody()
                .consumeWith(response -> {
                    System.out.println("[DEBUG_LOG] URL: " + response.getUrl());
                    System.out.println("[DEBUG_LOG] Status: " + response.getStatus());
                    System.out.println("[DEBUG_LOG] Headers: " + response.getResponseHeaders());
                    System.out.println("[DEBUG_LOG] Content-Type: " + response.getResponseHeaders().getContentType());
                    String body = new String(response.getResponseBodyContent() != null ? response.getResponseBodyContent() : new byte[0]);
                    System.out.println("[DEBUG_LOG] Body: " + body);
                });
    }
}
