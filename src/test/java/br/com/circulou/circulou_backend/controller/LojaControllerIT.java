package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.integration.BaseIntegrationTest;
import br.com.circulou.circulou_backend.dto.LojaRequestDTO;
import br.com.circulou.circulou_backend.dto.LojaResponseDTO;
import br.com.circulou.circulou_backend.dto.UsuarioRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LojaControllerIT extends BaseIntegrationTest {

    @Test
    @DisplayName("Deve realizar o CRUD completo de Loja")
    void deveRealizarCrudCompletoLoja() throws Exception {
        // 1. Criar Loja (POST)
        LojaRequestDTO requestDTO = new LojaRequestDTO();
        requestDTO.setNome("Burger King Test");
        requestDTO.setEmail("bk@test.com");
        requestDTO.setSenha("senha123");
        requestDTO.setTelefone("1144445555");
        requestDTO.setTempoMedioPreparo(30);
        requestDTO.setAtiva(true);

        // 0. Autenticar (Bootstrap)
        UsuarioRequestDTO usuarioDTO = new UsuarioRequestDTO();
        usuarioDTO.setNome("User Loja");
        usuarioDTO.setEmail("user.loja@test.com");
        usuarioDTO.setSenha("senha123");
        usuarioDTO.setTelefone("11999999999");
        usuarioDTO.setRole("USER");
        usuarioDTO.setAtivo(true);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isOk());
        String token = obterTokenAutenticacao(usuarioDTO.getEmail(), "senha123");

        // 1. Criar Loja (POST) - Agora autenticado
        MvcResult resultPost = mockMvc.perform(post("/lojas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        LojaResponseDTO responseDTO = objectMapper.readValue(resultPost.getResponse().getContentAsString(), LojaResponseDTO.class);
        assertThat(responseDTO.getId()).isNotNull();
        assertThat(responseDTO.getNome()).isEqualTo(requestDTO.getNome());

        Long lojaId = responseDTO.getId();

        // 2. Buscar por ID (GET)
        mockMvc.perform(get("/lojas/" + lojaId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    LojaResponseDTO found = objectMapper.readValue(result.getResponse().getContentAsString(), LojaResponseDTO.class);
                    assertThat(found.getNome()).isEqualTo(requestDTO.getNome());
                });

        // 3. Listar Todas (GET)
        mockMvc.perform(get("/lojas"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<?> list = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
                    assertThat(list).isNotEmpty();
                });

        // 4. Atualizar (PUT)
        requestDTO.setNome("BK Updated");
        mockMvc.perform(put("/lojas/" + lojaId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    LojaResponseDTO updated = objectMapper.readValue(result.getResponse().getContentAsString(), LojaResponseDTO.class);
                    assertThat(updated.getNome()).isEqualTo("BK Updated");
                });

        // 5. Deletar (DELETE)
        mockMvc.perform(delete("/lojas/" + lojaId)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        // 6. Verificar que não existe mais (GET 404)
        mockMvc.perform(get("/lojas/" + lojaId))
                .andExpect(status().isNotFound());
    }
}
