package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.dto.*;
import br.com.circulou.circulou_backend.integration.BaseIntegrationTest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PedidoClienteControllerIT extends BaseIntegrationTest {

    private Long usuarioId;
    private String token;
    private Long lojaId;
    private Long ofertaId;

    @BeforeEach
    void setUp() throws Exception {
        // Criar Usuário e Token para o Cliente
        token = criarUsuarioEObterToken("cliente.fluxo@test.com");
        usuarioId = usuarioRepositoryPort.findByEmail("cliente.fluxo@test.com").get().getId();

        // Configuração básica (Loja, Produto, Oferta) usando o token do cliente (que tem permissão para criar catálogo na V1)
        Long lojistaProfileId = criarLojistaProfile(token);

        LojaRequestDTO lojaDTO = new LojaRequestDTO();
        lojaDTO.setNome("Loja Cliente");
        lojaDTO.setEmail("loja.cliente@test.com");
        lojaDTO.setTelefone("1133332222");
        lojaDTO.setTempoMedioPreparo(30);
        lojaDTO.setAtiva(true);
        lojaDTO.setLojistaProfileId(lojistaProfileId);
        
        MvcResult resL = mockMvc.perform(post("/lojas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lojaDTO)))
                .andReturn();
        lojaId = objectMapper.readValue(resL.getResponse().getContentAsString(), LojaResponseDTO.class).getId();

        ProdutoRequestDTO produtoDTO = new ProdutoRequestDTO();
        produtoDTO.setNome("Produto Cliente");
        produtoDTO.setDescricao("Desc");
        produtoDTO.setMarca("Marca");
        produtoDTO.setUnidadeMedida("un");
        produtoDTO.setPeso(new BigDecimal("1.000"));
        produtoDTO.setCodigoBarras("7890000000001");
        produtoDTO.setAtivo(true);
        MvcResult resP = mockMvc.perform(post("/produtos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoDTO)))
                .andReturn();
        Long produtoId = objectMapper.readValue(resP.getResponse().getContentAsString(), ProdutoResponseDTO.class).getId();

        OfertaRequestDTO ofertaDTO = new OfertaRequestDTO();
        ofertaDTO.setLojaId(lojaId);
        ofertaDTO.setProdutoId(produtoId);
        ofertaDTO.setPreco(new BigDecimal("50.00"));
        ofertaDTO.setEstoque(10);
        ofertaDTO.setEstoqueMinimo(1);
        ofertaDTO.setAtivo(true);
        ofertaDTO.setDisponivel(true);
        MvcResult resO = mockMvc.perform(post("/ofertas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ofertaDTO)))
                .andReturn();
        ofertaId = objectMapper.readValue(resO.getResponse().getContentAsString(), OfertaResponseDTO.class).getId();
    }

    @Test
    @DisplayName("Deve permitir ao cliente visualizar seus próprios pedidos e detalhá-los")
    void devePermitirClienteVisualizarSeusPedidos() throws Exception {
        // 1. Criar um pedido para o cliente
        PedidoRequestDTO requestDTO = new PedidoRequestDTO();
        requestDTO.setUsuarioId(usuarioId);
        requestDTO.setLojaId(lojaId);
        ItemPedidoSimplesDTO itemDto = new ItemPedidoSimplesDTO();
        itemDto.setOfertaId(ofertaId);
        itemDto.setQuantidade(1);
        requestDTO.setItens(List.of(itemDto));

        MvcResult resPedido = mockMvc.perform(post("/pedidos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long pedidoId = objectMapper.readValue(resPedido.getResponse().getContentAsString(), PedidoResponseDTO.class).getId();

        // 2. Consultar "meus pedidos"
        mockMvc.perform(get("/pedidos/meus")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(pedidoId));

        // 3. Consultar detalhe do pedido
        mockMvc.perform(get("/pedidos/" + pedidoId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pedidoId))
                .andExpect(jsonPath("$.status").value("AGUARDANDO_LIBERACAO"));
    }

    @Test
    @DisplayName("Deve impedir cliente de visualizar pedido de outro usuário")
    void deveImpedirClienteDeVisualizarPedidoDeOutro() throws Exception {
        // 1. Criar pedido para o Cliente A
        PedidoRequestDTO requestDTO = new PedidoRequestDTO();
        requestDTO.setUsuarioId(usuarioId);
        requestDTO.setLojaId(lojaId);
        ItemPedidoSimplesDTO itemDto = new ItemPedidoSimplesDTO();
        itemDto.setOfertaId(ofertaId);
        itemDto.setQuantidade(1);
        requestDTO.setItens(List.of(itemDto));

        MvcResult resPedido = mockMvc.perform(post("/pedidos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn();
        Long pedidoId = objectMapper.readValue(resPedido.getResponse().getContentAsString(), PedidoResponseDTO.class).getId();

        // 2. Criar Cliente B e tentar acessar o pedido do Cliente A
        String tokenB = criarUsuarioEObterToken("clienteB@test.com");
        
        mockMvc.perform(get("/pedidos/" + pedidoId)
                        .header("Authorization", tokenB))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.erro").value("Acesso negado"));
    }

    @Test
    @DisplayName("Deve permitir cancelamento de pedido pelo cliente dentro do prazo")
    void devePermitirCancelamentoPeloCliente() throws Exception {
        // 1. Criar pedido
        PedidoRequestDTO requestDTO = new PedidoRequestDTO();
        requestDTO.setUsuarioId(usuarioId);
        requestDTO.setLojaId(lojaId);
        ItemPedidoSimplesDTO itemDto = new ItemPedidoSimplesDTO();
        itemDto.setOfertaId(ofertaId);
        itemDto.setQuantidade(1);
        requestDTO.setItens(List.of(itemDto));

        MvcResult resPedido = mockMvc.perform(post("/pedidos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn();
        Long pedidoId = objectMapper.readValue(resPedido.getResponse().getContentAsString(), PedidoResponseDTO.class).getId();

        // 2. Cancelar pedido
        mockMvc.perform(patch("/pedidos/" + pedidoId + "/cancelar")
                        .header("Authorization", token))
                .andExpect(status().isOk());

        // 3. Verificar status
        mockMvc.perform(get("/pedidos/" + pedidoId)
                        .header("Authorization", token))
                .andExpect(jsonPath("$.status").value("CANCELADO"));
    }

    @Test
    @DisplayName("Deve impedir cancelamento de pedido de outro usuário")
    void deveImpedirCancelamentoDePedidoDeOutro() throws Exception {
        // 1. Criar pedido para Cliente A
        PedidoRequestDTO requestDTO = new PedidoRequestDTO();
        requestDTO.setUsuarioId(usuarioId);
        requestDTO.setLojaId(lojaId);
        ItemPedidoSimplesDTO itemDto = new ItemPedidoSimplesDTO();
        itemDto.setOfertaId(ofertaId);
        itemDto.setQuantidade(1);
        requestDTO.setItens(List.of(itemDto));

        MvcResult resPedido = mockMvc.perform(post("/pedidos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn();
        Long pedidoId = objectMapper.readValue(resPedido.getResponse().getContentAsString(), PedidoResponseDTO.class).getId();

        // 2. Tentar cancelar com Cliente B
        String tokenB = criarUsuarioEObterToken("clienteB.cancelar@test.com");
        mockMvc.perform(patch("/pedidos/" + pedidoId + "/cancelar")
                        .header("Authorization", tokenB))
                .andExpect(status().isForbidden());
    }
}
