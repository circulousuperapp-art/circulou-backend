package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.integration.BaseIntegrationTest;
import br.com.circulou.circulou_backend.model.Jornada;
import br.com.circulou.circulou_backend.model.JornadaTipo;
import br.com.circulou.circulou_backend.port.in.JornadaUseCase;
import br.com.circulou.circulou_backend.port.in.command.CriarJornadaCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JornadaControllerIT extends BaseIntegrationTest {

    @Autowired
    private JornadaUseCase jornadaUseCase;

    private Long jornadaId;
    private Long usuarioDonoId;
    private String tokenDono;
    private String tokenEstranho;

    @BeforeEach
    void setUp() throws Exception {
        tokenDono = criarUsuarioEObterToken("dono@test.com");
        usuarioDonoId = usuarioRepositoryPort.findByEmail("dono@test.com").orElseThrow().getId();

        tokenEstranho = criarUsuarioEObterToken("estranho@test.com");

        CriarJornadaCommand.Parada parada = new CriarJornadaCommand.Parada(1L, 10L, 1);
        CriarJornadaCommand command = new CriarJornadaCommand(usuarioDonoId, JornadaTipo.PROPRIA, List.of(parada));

        Jornada salva = jornadaUseCase.criar(command);
        jornadaId = salva.getId();
    }

    @Test
    @DisplayName("GET /jornadas/{id} deve retornar 200 para o dono")
    void deveBuscarPorId() throws Exception {
        mockMvc.perform(get("/jornadas/{id}", jornadaId)
                        .header("Authorization", tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(jornadaId))
                .andExpect(jsonPath("$.clienteId").value(usuarioDonoId));
    }

    @Test
    @DisplayName("GET /jornadas/{id} deve retornar 403 para usuário diferente")
    void deveRetornar403AoBuscarJornadaAlheia() throws Exception {
        mockMvc.perform(get("/jornadas/{id}", jornadaId)
                        .header("Authorization", tokenEstranho))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /jornadas/{id}/iniciar deve retornar 200 para o dono")
    void deveIniciarJornada() throws Exception {
        mockMvc.perform(patch("/jornadas/{id}/iniciar", jornadaId)
                        .header("Authorization", tokenDono))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /jornadas/{id}/iniciar deve retornar 403 para usuário diferente")
    void deveBloquearInicioPorEstranho() throws Exception {
        mockMvc.perform(patch("/jornadas/{id}/iniciar", jornadaId)
                        .header("Authorization", tokenEstranho))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /jornadas/{id}/proxima-parada deve retornar 200 para o dono")
    void deveIniciarProximaParada() throws Exception {
        // Preparar estado da jornada: CRIADA -> EM_EXECUCAO
        jornadaUseCase.iniciar(jornadaId);

        mockMvc.perform(patch("/jornadas/{id}/proxima-parada", jornadaId)
                        .header("Authorization", tokenDono))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /jornadas/{id}/chegada-loja deve retornar 200 para o dono")
    void deveRegistrarChegada() throws Exception {
        // Preparar estado: CRIADA -> EM_EXECUCAO -> EM_TRANSITO
        jornadaUseCase.iniciar(jornadaId);
        jornadaUseCase.iniciarDeslocamentoProximaParada(jornadaId);

        mockMvc.perform(patch("/jornadas/{id}/chegada-loja", jornadaId)
                        .header("Authorization", tokenDono))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /jornadas/{id}/confirmar-retirada deve retornar 200 para o dono")
    void deveConfirmarRetirada() throws Exception {
        // Preparar estado: CRIADA -> EM_EXECUCAO -> EM_TRANSITO -> NA_LOJA
        jornadaUseCase.iniciar(jornadaId);
        jornadaUseCase.iniciarDeslocamentoProximaParada(jornadaId);
        jornadaUseCase.registrarChegadaNaLoja(jornadaId);

        mockMvc.perform(patch("/jornadas/{id}/confirmar-retirada", jornadaId)
                        .header("Authorization", tokenDono))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /jornadas/{id}/paradas/{ordem}/cancelar deve retornar 200 para o dono")
    void deveCancelarParada() throws Exception {
        mockMvc.perform(patch("/jornadas/{id}/paradas/{ordem}/cancelar", jornadaId, 1)
                        .header("Authorization", tokenDono))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /jornadas/{id}/concluir deve retornar 200 para o dono")
    void deveConcluirJornada() throws Exception {
        // Preparar estado terminal das paradas
        jornadaUseCase.iniciar(jornadaId);
        jornadaUseCase.iniciarDeslocamentoProximaParada(jornadaId);
        jornadaUseCase.registrarChegadaNaLoja(jornadaId);
        jornadaUseCase.confirmarRetirada(jornadaId);

        mockMvc.perform(patch("/jornadas/{id}/concluir", jornadaId)
                        .header("Authorization", tokenDono))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /jornadas/{id}/cancelar deve retornar 200 para o dono")
    void deveCancelarJornada() throws Exception {
        mockMvc.perform(patch("/jornadas/{id}/cancelar", jornadaId)
                        .header("Authorization", tokenDono))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 401 ao acessar sem token")
    void deveRetornar401SemToken() throws Exception {
        mockMvc.perform(get("/jornadas/{id}", jornadaId))
                .andExpect(status().isUnauthorized());
    }
}
