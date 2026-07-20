package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.integration.BaseIntegrationTest;
import br.com.circulou.circulou_backend.dto.*;
import br.com.circulou.circulou_backend.model.PedidoStatus;
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

class PedidoControllerIT extends BaseIntegrationTest {

    private Long usuarioId;
    private Long lojaId;
    private Long ofertaId;
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        // Criar Usuário e Token
        token = criarUsuarioEObterToken("comprador@test.com");
        usuarioId = usuarioRepositoryPort.findByEmail("comprador@test.com").get().getId();

        // Criar Lojista Profile Dinâmico
        Long lojistaProfileId = criarLojistaProfile(token);

        // Criar Loja Dinâmica
        LojaRequestDTO lojaDTO = new LojaRequestDTO();
        lojaDTO.setNome("Loja Pedidos");
        lojaDTO.setEmail("loja.pedidos@test.com");
        lojaDTO.setTelefone("1133332222");
        lojaDTO.setTempoMedioPreparo(25);
        lojaDTO.setAtiva(true);
        lojaDTO.setLojistaProfileId(lojistaProfileId);
        
        MvcResult resL = mockMvc.perform(post("/lojas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lojaDTO)))
                .andExpect(status().isOk())
                .andReturn();
        lojaId = objectMapper.readValue(resL.getResponse().getContentAsString(), LojaResponseDTO.class).getId();

        // Criar Produto Dinâmico
        ProdutoRequestDTO produtoDTO = new ProdutoRequestDTO();
        produtoDTO.setNome("Produto Pedido");
        produtoDTO.setDescricao("Desc");
        produtoDTO.setMarca("Marca");
        produtoDTO.setUnidadeMedida("un");
        produtoDTO.setPeso(1.0);
        produtoDTO.setCodigoBarras("7891234567890");
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
        ofertaDTO.setPreco(new BigDecimal("75.00"));
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
    }

    @Test
    @DisplayName("Deve realizar o fluxo comercial de Pedido com Autenticação Real")
    void deveRealizarFluxoComercialPedido() throws Exception {
        // 1. Criar Pedido (POST)
        PedidoRequestDTO requestDTO = new PedidoRequestDTO();
        requestDTO.setUsuarioId(usuarioId);
        requestDTO.setLojaId(lojaId);
        
        ItemPedidoSimplesDTO itemDto = new ItemPedidoSimplesDTO();
        itemDto.setOfertaId(ofertaId);
        itemDto.setQuantidade(2);
        requestDTO.setItens(List.of(itemDto));

        MvcResult resultPost = mockMvc.perform(post("/pedidos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        PedidoResponseDTO responseDTO = objectMapper.readValue(resultPost.getResponse().getContentAsString(), PedidoResponseDTO.class);
        assertThat(responseDTO.getId()).isNotNull();
        // 75.00 * 2 = 150.00
        assertThat(responseDTO.getValorTotal()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(responseDTO.getStatus()).isEqualTo(PedidoStatus.AGUARDANDO_LIBERACAO);
        assertThat(responseDTO.getItens()).hasSize(1);
        assertThat(responseDTO.getItens().get(0).getNomeProduto()).isEqualTo("Produto Pedido");

        Long pedidoId = responseDTO.getId();

        // 2. Buscar por ID (GET)
        mockMvc.perform(get("/pedidos/" + pedidoId)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        // 3. Listar Todos (GET)
        mockMvc.perform(get("/pedidos")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<?> list = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
                    assertThat(list).isNotEmpty();
                });

        // 4. Deletar (DELETE)
        mockMvc.perform(delete("/pedidos/" + pedidoId)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        // 5. Verificar que não existe mais (GET 404) - Pedido ainda usa deleção física por enquanto
        mockMvc.perform(get("/pedidos/" + pedidoId)
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar acessar pedidos sem autenticação")
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isUnauthorized());
    }
}
