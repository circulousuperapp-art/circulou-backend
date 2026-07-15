package br.com.circulou.circulou_backend.service;

import br.com.circulou.circulou_backend.exception.BusinessException;
import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.*;
import br.com.circulou.circulou_backend.port.out.PedidoRepositoryPort;
import br.com.circulou.circulou_backend.service.impl.PedidoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @Mock
    private OfertaService ofertaService;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Pedido pedido;
    private Usuario usuario;
    private Loja loja;
    private Oferta oferta;
    private Produto produto;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        loja = new Loja();
        loja.setId(1L);
        loja.setAtiva(true);

        produto = new Produto();
        produto.setId(1L);
        produto.setAtivo(true);
        produto.setNome("Produto Teste");

        oferta = new Oferta();
        oferta.setId(1L);
        oferta.setPreco(new BigDecimal("100.00"));
        oferta.setEstoque(10);
        oferta.setAtivo(true);
        oferta.setDisponivel(true);
        oferta.setLoja(loja);
        oferta.setProduto(produto);

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setQuantidade(2);
        item.setOferta(oferta);
        item.setPrecoUnitario(oferta.getPreco());
        item.setNomeProduto(produto.getNome());

        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(usuario);
        pedido.setLoja(loja);
        pedido.setItens(new ArrayList<>(List.of(item)));
        item.setPedido(pedido);
    }

    @Test
    @DisplayName("Deve listar todos os pedidos")
    void deveListarTodosPedidos() {
        when(pedidoRepositoryPort.findAll()).thenReturn(List.of(pedido));

        List<Pedido> resultado = pedidoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(pedidoRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    void deveBuscarPedidoPorId() {
        when(pedidoRepositoryPort.findById(1L)).thenReturn(Optional.of(pedido));

        Pedido resultado = pedidoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(pedido.getId(), resultado.getId());
    }

    @Test
    @DisplayName("Deve salvar pedido com sucesso e decrementar estoque")
    void deveSalvarPedidoComSucesso() {
        when(pedidoRepositoryPort.save(any(Pedido.class))).thenReturn(pedido);

        Pedido resultado = pedidoService.salvar(pedido);

        assertNotNull(resultado);
        assertEquals(new BigDecimal("200.00"), resultado.getValorTotal());
        assertEquals(8, oferta.getEstoque()); // 10 - 2
        verify(ofertaService, times(1)).atualizar(eq(1L), any(Oferta.class));
        verify(pedidoRepositoryPort, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao salvar pedido sem itens")
    void deveLancarExcecaoAoSalvarPedidoSemItens() {
        pedido.setItens(new ArrayList<>());

        assertThrows(BusinessException.class, () -> pedidoService.salvar(pedido));
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando estoque é insuficiente")
    void deveLancarExcecaoEstoqueInsuficiente() {
        pedido.getItens().get(0).setQuantidade(11);

        assertThrows(BusinessException.class, () -> pedidoService.salvar(pedido));
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando loja está inativa")
    void deveLancarExcecaoLojaInativa() {
        loja.setAtiva(false);

        assertThrows(BusinessException.class, () -> pedidoService.salvar(pedido));
    }
}