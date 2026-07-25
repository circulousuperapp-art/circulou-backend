package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.dto.*;
import br.com.circulou.circulou_backend.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OfertaControllerIT extends BaseIntegrationTest {
    private String token;
    private Long lojaId;
    private Long produtoId;

    @BeforeEach
    void setUp() throws Exception {
        token = criarUsuarioEObterToken("lojista.oferta@test.com");
        Long lojistaProfileId = criarLojistaProfile(token);

        // Criar uma Loja
        LojaRequestDTO lojaDTO = new LojaRequestDTO();
        lojaDTO.setNome("Loja de Ofertas");
        lojaDTO.setEmail("contato@lojaofertas.com");
        lojaDTO.setTelefone("11999990000");
        lojaDTO.setTempoMedioPreparo(30);
        lojaDTO.setAtiva(true);
        lojaDTO.setLojistaProfileId(lojistaProfileId);

        MvcResult resultLoja = mockMvc.perform(post("/lojas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lojaDTO)))
                .andExpect(status().isOk())
                .andReturn();
        lojaId = objectMapper.readValue(resultLoja.getResponse().getContentAsString(), LojaResponseDTO.class).getId();

        // Criar um Produto
        ProdutoRequestDTO produtoDTO = new ProdutoRequestDTO();
        produtoDTO.setNome("Produto Base");
        produtoDTO.setDescricao("Descrição");
        produtoDTO.setMarca("Marca");
        produtoDTO.setUnidadeMedida("un");
        produtoDTO.setPeso(new BigDecimal("1.000"));
        produtoDTO.setCodigoBarras("789000111");
        produtoDTO.setCategoria("Alimentos");
        produtoDTO.setAtivo(true);

        MvcResult resultProduto = mockMvc.perform(post("/produtos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoDTO)))
                .andExpect(status().isOk())
                .andReturn();
        produtoId = objectMapper.readValue(resultProduto.getResponse().getContentAsString(), ProdutoResponseDTO.class).getId();
    }

    @Test
    @DisplayName("Deve realizar o CRUD completo de Oferta")
    void deveRealizarCrudCompletoOferta() throws Exception {
        // 1. Criar Oferta (POST)
        OfertaRequestDTO requestDTO = new OfertaRequestDTO();
        requestDTO.setLojaId(lojaId);
        requestDTO.setProdutoId(produtoId);
        requestDTO.setPreco(new BigDecimal("25.50"));
        requestDTO.setEstoque(50);
        requestDTO.setEstoqueMinimo(5);
        requestDTO.setAtivo(true);
        requestDTO.setDisponivel(true);
        requestDTO.setPermiteEntrega(true);
        requestDTO.setPermiteRetirada(true);

        MvcResult resultPost = mockMvc.perform(post("/ofertas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        OfertaResponseDTO responseDTO = objectMapper.readValue(resultPost.getResponse().getContentAsString(), OfertaResponseDTO.class);
        assertThat(responseDTO.getId()).isNotNull();
        assertThat(responseDTO.getPreco()).isEqualByComparingTo(requestDTO.getPreco());

        Long ofertaId = responseDTO.getId();

        // 2. Buscar por ID (GET)
        mockMvc.perform(get("/ofertas/" + ofertaId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    OfertaResponseDTO found = objectMapper.readValue(result.getResponse().getContentAsString(), OfertaResponseDTO.class);
                    assertThat(found.getPreco()).isEqualByComparingTo(requestDTO.getPreco());
                });

        // 3. Listar Todas (GET)
        mockMvc.perform(get("/ofertas"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<?> list = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
                    assertThat(list).isNotEmpty();
                });

        // 4. Atualizar (PUT)
        requestDTO.setPreco(new BigDecimal("29.90"));
        mockMvc.perform(put("/ofertas/" + ofertaId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    OfertaResponseDTO updated = objectMapper.readValue(result.getResponse().getContentAsString(), OfertaResponseDTO.class);
                    assertThat(updated.getPreco()).isEqualByComparingTo(new BigDecimal("29.90"));
                });

        // 5. Deletar (DELETE)
        mockMvc.perform(delete("/ofertas/" + ofertaId)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        // 6. Verificar deleção lógica (GET retorna ativo=false)
        mockMvc.perform(get("/ofertas/" + ofertaId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    OfertaResponseDTO deleted = objectMapper.readValue(result.getResponse().getContentAsString(), OfertaResponseDTO.class);
                    assertThat(deleted.getAtivo()).isFalse();
                });
    }

    @Test
    @DisplayName("Deve filtrar ofertas por categoria")
    void deveFiltrarOfertasPorCategoria() throws Exception {
        // Criar oferta
        OfertaRequestDTO requestDTO = new OfertaRequestDTO();
        requestDTO.setLojaId(lojaId);
        requestDTO.setProdutoId(produtoId);
        requestDTO.setPreco(new BigDecimal("10.00"));
        requestDTO.setEstoque(10);
        requestDTO.setEstoqueMinimo(1);
        requestDTO.setAtivo(true);
        requestDTO.setDisponivel(true);

        mockMvc.perform(post("/ofertas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

        // Filtrar por categoria existente
        mockMvc.perform(get("/ofertas")
                        .param("categoria", "Alimentos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("\"totalElements\":1");
                });

        // Filtrar por categoria inexistente
        mockMvc.perform(get("/ofertas")
                        .param("categoria", "Eletronicos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("\"totalElements\":0");
                });
    }

    @Test
    @DisplayName("Deve filtrar ofertas por busca textual")
    void deveFiltrarOfertasPorBuscaTextual() throws Exception {
        // Criar oferta
        OfertaRequestDTO requestDTO = new OfertaRequestDTO();
        requestDTO.setLojaId(lojaId);
        requestDTO.setProdutoId(produtoId);
        requestDTO.setPreco(new BigDecimal("10.00"));
        requestDTO.setEstoque(10);
        requestDTO.setEstoqueMinimo(1);
        requestDTO.setAtivo(true);
        requestDTO.setDisponivel(true);

        mockMvc.perform(post("/ofertas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

        // Filtrar por nome do produto (parcial)
        mockMvc.perform(get("/ofertas")
                        .param("busca", "Base")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("\"totalElements\":1");
                });

        // Filtrar por termo inexistente
        mockMvc.perform(get("/ofertas")
                        .param("busca", "Inexistente")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("\"totalElements\":0");
                });
    }
}
