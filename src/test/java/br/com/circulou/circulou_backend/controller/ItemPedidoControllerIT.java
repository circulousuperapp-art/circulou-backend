package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.integration.BaseIntegrationTest;
import br.com.circulou.circulou_backend.dto.*;
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

class ItemPedidoControllerIT extends BaseIntegrationTest {

    private Long pedidoId;
    private Long ofertaId;
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        // Criar Usuário e Token
        token = criarUsuarioEObterToken("user.test@test.com");
        Long usuarioId = usuarioRepositoryPort.findByEmail("user.test@test.com").get().getId();

        // Criar Lojista Profile Dinâmico
        Long lojistaProfileId = criarLojistaProfile(token);

        // Criar Loja Dinâmica
        LojaRequestDTO lojaDTO = new LojaRequestDTO();
        lojaDTO.setNome("Loja Itens");
        lojaDTO.setEmail("loja.itens@test.com");
        lojaDTO.setTelefone("1133331111");
        lojaDTO.setTempoMedioPreparo(15);
        lojaDTO.setAtiva(true);
        lojaDTO.setLojistaProfileId(lojistaProfileId);
        
        MvcResult resL = mockMvc.perform(post("/lojas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lojaDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long lojaId = objectMapper.readValue(resL.getResponse().getContentAsString(), LojaResponseDTO.class).getId();

        // Criar Produto Dinâmico
        ProdutoRequestDTO produtoDTO = new ProdutoRequestDTO();
        produtoDTO.setNome("Produto Teste");
        produtoDTO.setDescricao("Desc");
        produtoDTO.setMarca("Marca");
        produtoDTO.setUnidadeMedida("un");
        produtoDTO.setPeso(new BigDecimal("1.000"));
        produtoDTO.setCodigoBarras("7891234567891");
        produtoDTO.setAtivo(true);
        MvcResult resP = mockMvc.perform(post("/produtos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long produtoId = objectMapper.readValue(resP.getResponse().getContentAsString(), ProdutoResponseDTO.class).getId();

        // Criar Oferta Dinâmica
        OfertaRequestDTO ofertaDTO = new OfertaRequestDTO();
        ofertaDTO.setLojaId(lojaId);
        ofertaDTO.setProdutoId(produtoId);
        ofertaDTO.setPreco(new BigDecimal("50.00"));
        ofertaDTO.setEstoque(100);
        ofertaDTO.setEstoqueMinimo(10);
        ofertaDTO.setAtivo(true);
        ofertaDTO.setDisponivel(true);
        MvcResult resO = mockMvc.perform(post("/ofertas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ofertaDTO)))
                .andExpect(status().isOk())
                .andReturn();
        ofertaId = objectMapper.readValue(resO.getResponse().getContentAsString(), OfertaResponseDTO.class).getId();

        // Criar Pedido Vazio (com um item inicial)
        PedidoRequestDTO pedidoDTO = new PedidoRequestDTO();
        pedidoDTO.setUsuarioId(usuarioId);
        pedidoDTO.setLojaId(lojaId);
        ItemPedidoSimplesDTO itemPed = new ItemPedidoSimplesDTO(1, ofertaId);
        pedidoDTO.setItens(List.of(itemPed));

        MvcResult resPed = mockMvc.perform(post("/pedidos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isOk())
                .andReturn();
        pedidoId = objectMapper.readValue(resPed.getResponse().getContentAsString(), PedidoResponseDTO.class).getId();
    }

    @Test
    @DisplayName("Deve realizar o CRUD completo de ItemPedido com Autenticação Real")
    void deveRealizarCrudCompletoItemPedido() throws Exception {
        // 1. Criar Item (POST)
        ItemPedidoRequestDTO requestDTO = new ItemPedidoRequestDTO();
        requestDTO.setQuantidade(5);
        requestDTO.setPedidoId(pedidoId);
        requestDTO.setOfertaId(ofertaId);

        MvcResult resultPost = mockMvc.perform(post("/itens-pedido")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        ItemPedidoResponseDTO responseDTO = objectMapper.readValue(resultPost.getResponse().getContentAsString(), ItemPedidoResponseDTO.class);
        assertThat(responseDTO.getId()).isNotNull();
        assertThat(responseDTO.getQuantidade()).isEqualTo(5);
        assertThat(responseDTO.getNomeProduto()).isEqualTo("Produto Teste");
        assertThat(responseDTO.getPrecoUnitario()).isEqualByComparingTo(new BigDecimal("50.00"));

        Long itemId = responseDTO.getId();

        // 2. Buscar por ID (GET)
        mockMvc.perform(get("/itens-pedido/" + itemId)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        // 3. Listar Todos (GET)
        mockMvc.perform(get("/itens-pedido")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<?> list = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
                    assertThat(list).isNotEmpty();
                });

        // 4. Atualizar (PUT)
        requestDTO.setQuantidade(8);
        mockMvc.perform(put("/itens-pedido/" + itemId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

        // 5. Deletar (DELETE)
        mockMvc.perform(delete("/itens-pedido/" + itemId)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        // 6. Verificar que não existe mais (GET 404)
        mockMvc.perform(get("/itens-pedido/" + itemId)
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}
