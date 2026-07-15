package br.com.circulou.circulou_backend.service;

import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.model.Produto;
import br.com.circulou.circulou_backend.port.out.ProdutoRepositoryPort;
import br.com.circulou.circulou_backend.service.impl.ProdutoServiceImpl;
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
class ProdutoServiceTest {

    @Mock
    private ProdutoRepositoryPort produtoRepositoryPort;

    @InjectMocks
    private ProdutoServiceImpl produtoService;

    private Produto produto;
    private Loja loja;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Hambúrguer");
        produto.setDescricao("Pão, carne e queijo");
        produto.setMarca("Marca");
        produto.setUnidadeMedida("un");
        produto.setPeso(0.3);
        produto.setCodigoBarras("123456");
        produto.setAtivo(true);
    }

    @Test
    @DisplayName("Deve listar todos os produtos")
    void deveListarTodosProdutos() {
        when(produtoRepositoryPort.findAll()).thenReturn(List.of(produto));

        List<Produto> resultado = produtoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(produto.getNome(), resultado.get(0).getNome());
        verify(produtoRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void deveBuscarProdutoPorId() {
        when(produtoRepositoryPort.findById(1L)).thenReturn(Optional.of(produto));

        Produto resultado = produtoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(produto.getId(), resultado.getId());
        verify(produtoRepositoryPort, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar produto inexistente")
    void deveLancarExcecaoAoBuscarProdutoInexistente() {
        when(produtoRepositoryPort.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> produtoService.buscarPorId(2L));
        verify(produtoRepositoryPort, times(1)).findById(2L);
    }

    @Test
    @DisplayName("Deve salvar produto com sucesso")
    void deveSalvarProdutoComSucesso() {
        when(produtoRepositoryPort.save(any(Produto.class))).thenReturn(produto);

        Produto resultado = produtoService.salvar(produto);

        assertNotNull(resultado);
        verify(produtoRepositoryPort, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() {
        when(produtoRepositoryPort.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepositoryPort.save(any(Produto.class))).thenReturn(produto);

        Produto resultado = produtoService.atualizar(1L, produto);

        assertNotNull(resultado);
        verify(produtoRepositoryPort, times(1)).findById(1L);
        verify(produtoRepositoryPort, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar produto inexistente")
    void deveLancarExcecaoAoAtualizarProdutoInexistente() {
        when(produtoRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> produtoService.atualizar(1L, produto));
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void deveDeletarProdutoComSucesso() {
        when(produtoRepositoryPort.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepositoryPort.save(any(Produto.class))).thenReturn(produto);

        assertDoesNotThrow(() -> produtoService.deletar(1L));

        assertFalse(produto.getAtivo());
        verify(produtoRepositoryPort, times(1)).findById(1L);
        verify(produtoRepositoryPort, times(1)).save(produto);
        verify(produtoRepositoryPort, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar produto inexistente")
    void deveLancarExcecaoAoDeletarProdutoInexistente() {
        when(produtoRepositoryPort.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> produtoService.deletar(2L));

        verify(produtoRepositoryPort, times(1)).findById(2L);
        verify(produtoRepositoryPort, never()).deleteById(anyLong());
    }
}