package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.integration.BaseIntegrationTest;
import br.com.circulou.circulou_backend.dto.UsuarioRequestDTO;
import br.com.circulou.circulou_backend.dto.UsuarioResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UsuarioControllerIT extends BaseIntegrationTest {

    @Test
    @DisplayName("Deve realizar o CRUD completo de Usuário")
    void deveRealizarCrudCompletoUsuario() throws Exception {
        // 1. Criar Usuário (POST)
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setNome("Teste Integração");
        requestDTO.setEmail("teste.integration@email.com");
        requestDTO.setSenha("senha123");
        requestDTO.setTelefone("11999999999");
        requestDTO.setAtivo(true);

        MvcResult resultPost = mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        UsuarioResponseDTO responseDTO = objectMapper.readValue(resultPost.getResponse().getContentAsString(), UsuarioResponseDTO.class);
        assertThat(responseDTO.getId()).isNotNull();
        assertThat(responseDTO.getNome()).isEqualTo(requestDTO.getNome());

        Long usuarioId = responseDTO.getId();

        // 0. Autenticar para as próximas operações
        String token = obterTokenAutenticacao(requestDTO.getEmail(), "senha123");

        // 2. Buscar por ID (GET)
        mockMvc.perform(get("/usuarios/" + usuarioId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    UsuarioResponseDTO found = objectMapper.readValue(result.getResponse().getContentAsString(), UsuarioResponseDTO.class);
                    assertThat(found.getNome()).isEqualTo(requestDTO.getNome());
                });

        // 3. Listar Todos (GET)
        mockMvc.perform(get("/usuarios")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<?> list = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
                    assertThat(list).isNotEmpty();
                });

        // 4. Atualizar (PUT)
        requestDTO.setNome("Nome Atualizado");
        mockMvc.perform(put("/usuarios/" + usuarioId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    UsuarioResponseDTO updated = objectMapper.readValue(result.getResponse().getContentAsString(), UsuarioResponseDTO.class);
                    assertThat(updated.getNome()).isEqualTo("Nome Atualizado");
                });

        // 5. Deletar (DELETE)
        mockMvc.perform(delete("/usuarios/" + usuarioId)
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar acessar com token de usuário excluído")
    void deveRetornar403AoAcessarComTokenDeUsuarioExcluido() throws Exception {
        // 1. Criar Usuário
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setNome("Usuario Temporario");
        requestDTO.setEmail("temp@test.com");
        requestDTO.setSenha("senha123");
        requestDTO.setTelefone("11999999999");
        requestDTO.setAtivo(true);

        MvcResult resultPost = mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        UsuarioResponseDTO responseDTO = objectMapper.readValue(resultPost.getResponse().getContentAsString(), UsuarioResponseDTO.class);
        Long usuarioId = responseDTO.getId();

        // 2. Obter Token
        String token = obterTokenAutenticacao(requestDTO.getEmail(), "senha123");

        // 3. Deletar Usuário
        mockMvc.perform(delete("/usuarios/" + usuarioId)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        // 4. Tentar acessar recurso protegido com o mesmo token (deve ser 403 Forbidden)
        mockMvc.perform(get("/usuarios/" + usuarioId)
                        .header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar criar usuário inválido")
    void deveRetornar400AoCriarUsuarioInvalido() throws Exception {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(); // Faltam campos obrigatórios

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
}
