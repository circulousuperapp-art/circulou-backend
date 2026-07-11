package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.integration.BaseIntegrationTest;
import br.com.circulou.circulou_backend.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItemPedidoControllerIT extends BaseIntegrationTest {

    private Long pedidoId;
    private Long produtoId;
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        // Criar Usuário
        UsuarioRequestDTO usuarioDTO = new UsuarioRequestDTO();
        usuarioDTO.setNome("User Test");
        usuarioDTO.setEmail("user.test@test.com");
        usuarioDTO.setSenha("123456");
        usuarioDTO.setTelefone("11999998888");
        usuarioDTO.setRole("USER");
        usuarioDTO.setAtivo(true);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isOk());
        
        token = obterTokenAutenticacao(usuarioDTO.getEmail(), "123456");
        Long usuarioId = usuarioRepositoryPort.findByEmail(usuarioDTO.getEmail()).get().getId();

        // Criar Loja
        LojaRequestDTO lojaDTO = new LojaRequestDTO();
        lojaDTO.setNome("Loja Itens");
        lojaDTO.setEmail("loja.itens@test.com");
        lojaDTO.setSenha("123456");
        lojaDTO.setTelefone("1133331111");
        lojaDTO.setTempoMedioPreparo(15);
        MvcResult resL = mockMvc.perform(post("/lojas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lojaDTO)))
                .andReturn();
        Long lojaId = objectMapper.readValue(resL.getResponse().getContentAsString(), LojaResponseDTO.class).getId();

        // Criar Produto
        ProdutoRequestDTO produtoDTO = new ProdutoRequestDTO();
        produtoDTO.setNome("Produto Teste");
        produtoDTO.setDescricao("Desc");
        produtoDTO.setPreco(10.0);
        produtoDTO.setEstoque(10);
        produtoDTO.setCategoria("Cat");
        produtoDTO.setLojaId(lojaId);
        MvcResult resP = mockMvc.perform(post("/produtos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoDTO)))
                .andReturn();
        produtoId = objectMapper.readValue(resP.getResponse().getContentAsString(), ProdutoResponseDTO.class).getId();

        // Criar Pedido
        PedidoRequestDTO pedidoDTO = new PedidoRequestDTO();
        pedidoDTO.setValorTotal(100.0);
        pedidoDTO.setStatus("PENDENTE");
        pedidoDTO.setUsuarioId(usuarioId);
        pedidoDTO.setLojaId(lojaId);
        MvcResult resPed = mockMvc.perform(post("/pedidos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andReturn();
        pedidoId = objectMapper.readValue(resPed.getResponse().getContentAsString(), PedidoResponseDTO.class).getId();
    }

    @Test
    @DisplayName("Deve realizar o CRUD completo de ItemPedido com Autenticação Real")
    void deveRealizarCrudCompletoItemPedido() throws Exception {
        // 1. Criar Item (POST)
        ItemPedidoRequestDTO requestDTO = new ItemPedidoRequestDTO();
        requestDTO.setQuantidade(5);
        requestDTO.setPrecoUnitario(10.0);
        requestDTO.setSubtotal(50.0);
        requestDTO.setPedidoId(pedidoId);
        requestDTO.setProdutoId(produtoId);

        MvcResult resultPost = mockMvc.perform(post("/itens-pedido")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        ItemPedidoResponseDTO responseDTO = objectMapper.readValue(resultPost.getResponse().getContentAsString(), ItemPedidoResponseDTO.class);
        assertThat(responseDTO.getId()).isNotNull();
        assertThat(responseDTO.getQuantidade()).isEqualTo(5);

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
