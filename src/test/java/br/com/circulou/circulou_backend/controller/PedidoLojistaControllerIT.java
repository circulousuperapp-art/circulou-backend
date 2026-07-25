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

class PedidoLojistaControllerIT extends BaseIntegrationTest {

    private Long usuarioId;
    private Long lojaId;
    private Long ofertaId;
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        token = criarUsuarioEObterToken("lojista@test.com");
        usuarioId = usuarioRepositoryPort.findByEmail("lojista@test.com").get().getId();

        Long lojistaProfileId = criarLojistaProfile(token);

        LojaRequestDTO lojaDTO = new LojaRequestDTO();
        lojaDTO.setNome("Loja do Lojista");
        lojaDTO.setEmail("loja.lojista@test.com");
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

        ProdutoRequestDTO produtoDTO = new ProdutoRequestDTO();
        produtoDTO.setNome("Produto Lojista");
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
    }

    @Test
    @DisplayName("Deve realizar o fluxo completo do lojista no pedido")
    void deveRealizarFluxoCompletoLojista() throws Exception {
        // 1. Criar Pedido (Status inicial: AGUARDANDO_LIBERACAO)
        PedidoRequestDTO requestDTO = new PedidoRequestDTO();
        requestDTO.setUsuarioId(usuarioId);
        requestDTO.setLojaId(lojaId);
        
        ItemPedidoSimplesDTO itemDto = new ItemPedidoSimplesDTO();
        itemDto.setOfertaId(ofertaId);
        itemDto.setQuantidade(1);
        requestDTO.setItens(List.of(itemDto));

        MvcResult resultPost = mockMvc.perform(post("/pedidos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        PedidoResponseDTO pedido = objectMapper.readValue(resultPost.getResponse().getContentAsString(), PedidoResponseDTO.class);
        Long pedidoId = pedido.getId();
        assertThat(pedido.getStatus()).isEqualTo(PedidoStatus.AGUARDANDO_LIBERACAO);

        // 2. Aceitar Pedido (Deve mudar para EM_PREPARO)
        mockMvc.perform(patch("/pedidos/" + pedidoId + "/aceitar")
                        .header("Authorization", token))
                .andExpect(status().isOk());

        verificarStatus(pedidoId, PedidoStatus.EM_PREPARO);

        // 3. Colocar Em Preparação (Status já é EM_PREPARO pelo 'aceitar', mas vamos garantir o endpoint específico se existir ou se a transição permitir)
        // De acordo com o PedidoStatusPolicy: AGUARDANDO_LIBERACAO -> EM_PREPARO
        // O requisito diz: 2. Aceitação do pedido, 3. Alteração para "Em Preparação".
        // Se Aceitar já move para Em Preparo, talvez o 'preparar' seja redundante ou mova para outro status intermediário.
        // Vamos seguir o fluxo sugerido no EPIC: Aceitar -> Em Preparação -> Pronto para Retirada -> Finalizar.
        
        mockMvc.perform(patch("/pedidos/" + pedidoId + "/preparar")
                        .header("Authorization", token))
                .andExpect(status().isOk());

        verificarStatus(pedidoId, PedidoStatus.EM_PREPARO);

        // 4. Pronto para Retirada
        mockMvc.perform(patch("/pedidos/" + pedidoId + "/pronto")
                        .header("Authorization", token))
                .andExpect(status().isOk());

        verificarStatus(pedidoId, PedidoStatus.PRONTO_PARA_RETIRADA);

        // 5. Finalizar (Entregue)
        mockMvc.perform(patch("/pedidos/" + pedidoId + "/finalizar")
                        .header("Authorization", token))
                .andExpect(status().isOk());

        verificarStatus(pedidoId, PedidoStatus.ENTREGUE);
    }

    private void verificarStatus(Long pedidoId, PedidoStatus statusEsperado) throws Exception {
        mockMvc.perform(get("/pedidos/" + pedidoId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    PedidoResponseDTO p = objectMapper.readValue(result.getResponse().getContentAsString(), PedidoResponseDTO.class);
                    assertThat(p.getStatus()).isEqualTo(statusEsperado);
                });
    }
}
