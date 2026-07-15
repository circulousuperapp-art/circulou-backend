package br.com.circulou.circulou_backend.service;

import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.port.out.LojaRepositoryPort;
import br.com.circulou.circulou_backend.service.impl.LojaServiceImpl;
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
class LojaServiceTest {

    @Mock
    private LojaRepositoryPort lojaRepositoryPort;

    @InjectMocks
    private LojaServiceImpl lojaService;

    private Loja loja;

    @BeforeEach
    void setUp() {
        loja = new Loja();
        loja.setId(1L);
        loja.setNome("Burger House");
        loja.setEmail("contato@burger.com");
    }

    @Test
    @DisplayName("Deve listar todas as lojas com sucesso")
    void deveListarTodasLojas() {
        when(lojaRepositoryPort.findAll()).thenReturn(List.of(loja));

        List<Loja> resultado = lojaService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(loja.getNome(), resultado.get(0).getNome());
        verify(lojaRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar loja por ID com sucesso")
    void deveBuscarLojaPorId() {
        when(lojaRepositoryPort.findById(1L)).thenReturn(Optional.of(loja));

        Loja resultado = lojaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(loja.getId(), resultado.getId());
        verify(lojaRepositoryPort, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar loja inexistente")
    void deveLancarExcecaoAoBuscarLojaInexistente() {
        when(lojaRepositoryPort.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lojaService.buscarPorId(2L));
        verify(lojaRepositoryPort, times(1)).findById(2L);
    }

    @Test
    @DisplayName("Deve salvar nova loja com sucesso")
    void deveSalvarLojaComSucesso() {
        when(lojaRepositoryPort.save(any(Loja.class))).thenReturn(loja);

        Loja resultado = lojaService.salvar(loja);

        assertNotNull(resultado);
        assertEquals(loja.getNome(), resultado.getNome());
        verify(lojaRepositoryPort, times(1)).save(any(Loja.class));
    }

    @Test
    @DisplayName("Deve atualizar loja existente com sucesso")
    void deveAtualizarLojaComSucesso() {
        when(lojaRepositoryPort.findById(1L)).thenReturn(Optional.of(loja));
        when(lojaRepositoryPort.save(any(Loja.class))).thenReturn(loja);

        Loja resultado = lojaService.atualizar(1L, loja);

        assertNotNull(resultado);
        verify(lojaRepositoryPort, times(1)).findById(1L);
        verify(lojaRepositoryPort, times(1)).save(any(Loja.class));
    }

    @Test
    @DisplayName("Deve deletar loja com sucesso")
    void deveDeletarLojaComSucesso() {
        when(lojaRepositoryPort.findById(1L)).thenReturn(Optional.of(loja));
        when(lojaRepositoryPort.save(any(Loja.class))).thenReturn(loja);

        assertDoesNotThrow(() -> lojaService.deletar(1L));

        assertFalse(loja.getAtiva());
        verify(lojaRepositoryPort, times(1)).findById(1L);
        verify(lojaRepositoryPort, times(1)).save(loja);
        verify(lojaRepositoryPort, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar loja inexistente")
    void deveLancarExcecaoAoDeletarLojaInexistente() {
        when(lojaRepositoryPort.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lojaService.deletar(2L));

        verify(lojaRepositoryPort, times(1)).findById(2L);
        verify(lojaRepositoryPort, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar loja inexistente")
    void deveLancarExcecaoAoAtualizarLojaInexistente() {
        when(lojaRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lojaService.atualizar(1L, loja));
    }
}