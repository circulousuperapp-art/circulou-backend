package br.com.circulou.circulou_backend.service;

import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.ItemPedido;
import br.com.circulou.circulou_backend.model.Pedido;
import br.com.circulou.circulou_backend.model.Produto;
import br.com.circulou.circulou_backend.port.out.ItemPedidoRepositoryPort;
import br.com.circulou.circulou_backend.service.impl.ItemPedidoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemPedidoServiceTest {

    @Mock
    private ItemPedidoRepositoryPort itemPedidoRepositoryPort;

    @InjectMocks
    private ItemPedidoServiceImpl itemPedidoService;

    private ItemPedido itemPedido;
    private Pedido pedido;
    private Produto produto;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setId(1L);

        produto = new Produto();
        produto.setId(1L);

        itemPedido = new ItemPedido();
        itemPedido.setId(1L);
        itemPedido.setQuantidade(2);
        itemPedido.setPedido(pedido);
        itemPedido.setProduto(produto);
    }

    @Test
    @DisplayName("Deve listar todos os itens de pedido")
    void deveListarTodosItensPedido() {
        when(itemPedidoRepositoryPort.findAll()).thenReturn(List.of(itemPedido));

        List<ItemPedido> resultado = itemPedidoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(itemPedidoRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar item de pedido por ID com sucesso")
    void deveBuscarItemPedidoPorId() {
        when(itemPedidoRepositoryPort.findById(1L)).thenReturn(Optional.of(itemPedido));

        ItemPedido resultado = itemPedidoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(itemPedido.getId(), resultado.getId());
        verify(itemPedidoRepositoryPort, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar item inexistente")
    void deveLancarExcecaoAoBuscarItemInexistente() {
        when(itemPedidoRepositoryPort.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemPedidoService.buscarPorId(2L));
        verify(itemPedidoRepositoryPort, times(1)).findById(2L);
    }

    @Test
    @DisplayName("Deve salvar item de pedido com sucesso")
    void deveSalvarItemPedidoComSucesso() {
        when(itemPedidoRepositoryPort.save(any(ItemPedido.class))).thenReturn(itemPedido);

        ItemPedido resultado = itemPedidoService.salvar(itemPedido);

        assertNotNull(resultado);
        verify(itemPedidoRepositoryPort, times(1)).save(any(ItemPedido.class));
    }

    @Test
    @DisplayName("Deve atualizar item de pedido com sucesso")
    void deveAtualizarItemPedidoComSucesso() {
        when(itemPedidoRepositoryPort.findById(1L)).thenReturn(Optional.of(itemPedido));
        when(itemPedidoRepositoryPort.save(any(ItemPedido.class))).thenReturn(itemPedido);

        ItemPedido resultado = itemPedidoService.atualizar(1L, itemPedido);

        assertNotNull(resultado);
        verify(itemPedidoRepositoryPort, times(1)).findById(1L);
        verify(itemPedidoRepositoryPort, times(1)).save(any(ItemPedido.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar item inexistente")
    void deveLancarExcecaoAoAtualizarItemInexistente() {
        when(itemPedidoRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemPedidoService.atualizar(1L, itemPedido));
    }

    @Test
    @DisplayName("Deve deletar item de pedido com sucesso")
    void deveDeletarItemPedidoComSucesso() {
        when(itemPedidoRepositoryPort.existsById(1L)).thenReturn(true);
        doNothing().when(itemPedidoRepositoryPort).deleteById(1L);

        assertDoesNotThrow(() -> itemPedidoService.deletar(1L));

        verify(itemPedidoRepositoryPort, times(1)).existsById(1L);
        verify(itemPedidoRepositoryPort, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar item inexistente")
    void deveLancarExcecaoAoDeletarItemInexistente() {
        when(itemPedidoRepositoryPort.existsById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> itemPedidoService.deletar(2L));

        verify(itemPedidoRepositoryPort, times(1)).existsById(2L);
        verify(itemPedidoRepositoryPort, never()).deleteById(anyLong());
    }
}