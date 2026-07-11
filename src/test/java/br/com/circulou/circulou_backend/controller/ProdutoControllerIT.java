package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.integration.BaseIntegrationTest;
import br.com.circulou.circulou_backend.dto.LojaRequestDTO;
import br.com.circulou.circulou_backend.dto.LojaResponseDTO;
import br.com.circulou.circulou_backend.dto.ProdutoRequestDTO;
import br.com.circulou.circulou_backend.dto.ProdutoResponseDTO;
import br.com.circulou.circulou_backend.dto.UsuarioRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProdutoControllerIT extends BaseIntegrationTest {

    private Long lojaId;
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        // Criar usuário para autenticação
        UsuarioRequestDTO usuarioDTO = new UsuarioRequestDTO();
        usuarioDTO.setNome("User Produto");
        usuarioDTO.setEmail("user.produto@test.com");
        usuarioDTO.setSenha("senha123");
        usuarioDTO.setTelefone("11999999999");
        usuarioDTO.setRole("USER");
        usuarioDTO.setAtivo(true);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isOk());
        token = obterTokenAutenticacao(usuarioDTO.getEmail(), "senha123");

        LojaRequestDTO lojaDTO = new LojaRequestDTO();
        lojaDTO.setNome("Loja de Produtos");
        lojaDTO.setEmail("loja.produtos@test.com");
        lojaDTO.setSenha("123456");
        lojaDTO.setTelefone("1133334444");
        lojaDTO.setTempoMedioPreparo(20);

        MvcResult result = mockMvc.perform(post("/lojas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lojaDTO)))
                .andReturn();

        LojaResponseDTO response = objectMapper.readValue(result.getResponse().getContentAsString(), LojaResponseDTO.class);
        lojaId = response.getId();
    }

    @Test
    @DisplayName("Deve realizar o CRUD completo de Produto")
    void deveRealizarCrudCompletoProduto() throws Exception {
        // 1. Criar Produto (POST)
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("X-Burger");
        requestDTO.setDescricao("Pão, carne e queijo");
        requestDTO.setPreco(25.0);
        requestDTO.setEstoque(100);
        requestDTO.setCategoria("Lanches");
        requestDTO.setAtivo(true);
        requestDTO.setLojaId(lojaId);

        MvcResult resultPost = mockMvc.perform(post("/produtos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        ProdutoResponseDTO responseDTO = objectMapper.readValue(resultPost.getResponse().getContentAsString(), ProdutoResponseDTO.class);
        assertThat(responseDTO.getId()).isNotNull();
        assertThat(responseDTO.getNome()).isEqualTo(requestDTO.getNome());

        Long produtoId = responseDTO.getId();

        // 2. Buscar por ID (GET)
        mockMvc.perform(get("/produtos/" + produtoId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ProdutoResponseDTO found = objectMapper.readValue(result.getResponse().getContentAsString(), ProdutoResponseDTO.class);
                    assertThat(found.getNome()).isEqualTo(requestDTO.getNome());
                });

        // 3. Listar Todos (GET)
        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<?> list = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
                    assertThat(list).isNotEmpty();
                });

        // 4. Atualizar (PUT)
        requestDTO.setNome("X-Burger Especial");
        mockMvc.perform(put("/produtos/" + produtoId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ProdutoResponseDTO updated = objectMapper.readValue(result.getResponse().getContentAsString(), ProdutoResponseDTO.class);
                    assertThat(updated.getNome()).isEqualTo("X-Burger Especial");
                });

        // 5. Deletar (DELETE)
        mockMvc.perform(delete("/produtos/" + produtoId)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        // 6. Verificar que não existe mais (GET 404)
        mockMvc.perform(get("/produtos/" + produtoId))
                .andExpect(status().isNotFound());
    }
}
