package br.com.circulou.circulou_backend.service;

import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.Usuario;
import br.com.circulou.circulou_backend.port.out.UsuarioRepositoryPort;
import br.com.circulou.circulou_backend.service.impl.UsuarioServiceImpl;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("encoded_senha");
    }

    @Test
    @DisplayName("Deve listar todos os usuários com sucesso")
    void deveListarTodosUsuarios() {
        when(usuarioRepositoryPort.findAll()).thenReturn(List.of(usuario));

        List<Usuario> resultado = usuarioService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(usuario.getNome(), resultado.get(0).getNome());
        verify(usuarioRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorId() {
        when(usuarioRepositoryPort.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(usuario.getId(), resultado.getId());
        verify(usuarioRepositoryPort, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar ID inexistente")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        when(usuarioRepositoryPort.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.buscarPorId(2L));
        verify(usuarioRepositoryPort, times(1)).findById(2L);
    }

    @Test
    @DisplayName("Deve salvar novo usuário com sucesso")
    void deveSalvarUsuarioComSucesso() {
        when(usuarioRepositoryPort.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.salvar(usuario);

        assertNotNull(resultado);
        assertEquals(usuario.getNome(), resultado.getNome());
        verify(usuarioRepositoryPort, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve atualizar usuário existente com sucesso")
    void deveAtualizarUsuarioComSucesso() {
        when(usuarioRepositoryPort.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepositoryPort.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.atualizar(1L, usuario);

        assertNotNull(resultado);
        verify(usuarioRepositoryPort, times(1)).findById(1L);
        verify(usuarioRepositoryPort, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar usuário inexistente")
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        when(usuarioRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.atualizar(1L, usuario));
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso quando ID existe")
    void deveDeletarUsuarioComSucesso() {
        when(usuarioRepositoryPort.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepositoryPort).deleteById(1L);

        assertDoesNotThrow(() -> usuarioService.deletar(1L));

        verify(usuarioRepositoryPort, times(1)).existsById(1L);
        verify(usuarioRepositoryPort, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar ID inexistente")
    void deveLancarExcecaoAoDeletarIdInexistente() {
        when(usuarioRepositoryPort.existsById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.deletar(2L));

        verify(usuarioRepositoryPort, times(1)).existsById(2L);
        verify(usuarioRepositoryPort, never()).deleteById(anyLong());
    }
}