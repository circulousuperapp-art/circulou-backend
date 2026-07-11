package br.com.circulou.circulou_backend.service;

import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.model.Pedido;
import br.com.circulou.circulou_backend.model.Usuario;
import br.com.circulou.circulou_backend.port.out.PedidoRepositoryPort;
import br.com.circulou.circulou_backend.service.impl.PedidoServiceImpl;
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
class PedidoServiceTest {

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Pedido pedido;
    private Usuario usuario;
    private Loja loja;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        loja = new Loja();
        loja.setId(1L);

        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setValorTotal(100.0);
        pedido.setUsuario(usuario);
        pedido.setLoja(loja);
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
        verify(pedidoRepositoryPort, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar pedido inexistente")
    void deveLancarExcecaoAoBuscarPedidoInexistente() {
        when(pedidoRepositoryPort.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.buscarPorId(2L));
        verify(pedidoRepositoryPort, times(1)).findById(2L);
    }

    @Test
    @DisplayName("Deve salvar pedido com sucesso")
    void deveSalvarPedidoComSucesso() {
        when(pedidoRepositoryPort.save(any(Pedido.class))).thenReturn(pedido);

        Pedido resultado = pedidoService.salvar(pedido);

        assertNotNull(resultado);
        verify(pedidoRepositoryPort, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve atualizar pedido com sucesso")
    void deveAtualizarPedidoComSucesso() {
        when(pedidoRepositoryPort.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepositoryPort.save(any(Pedido.class))).thenReturn(pedido);

        Pedido resultado = pedidoService.atualizar(1L, pedido);

        assertNotNull(resultado);
        verify(pedidoRepositoryPort, times(1)).findById(1L);
        verify(pedidoRepositoryPort, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar pedido inexistente")
    void deveLancarExcecaoAoAtualizarPedidoInexistente() {
        when(pedidoRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.atualizar(1L, pedido));
    }

    @Test
    @DisplayName("Deve deletar pedido com sucesso")
    void deveDeletarPedidoComSucesso() {
        when(pedidoRepositoryPort.existsById(1L)).thenReturn(true);
        doNothing().when(pedidoRepositoryPort).deleteById(1L);

        assertDoesNotThrow(() -> pedidoService.deletar(1L));

        verify(pedidoRepositoryPort, times(1)).existsById(1L);
        verify(pedidoRepositoryPort, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar pedido inexistente")
    void deveLancarExcecaoAoDeletarPedidoInexistente() {
        when(pedidoRepositoryPort.existsById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.deletar(2L));

        verify(pedidoRepositoryPort, times(1)).existsById(2L);
        verify(pedidoRepositoryPort, never()).deleteById(anyLong());
    }
}