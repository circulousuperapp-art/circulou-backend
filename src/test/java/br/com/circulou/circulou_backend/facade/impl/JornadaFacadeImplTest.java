package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.JornadaResponseDTO;
import br.com.circulou.circulou_backend.exception.AccessDeniedException;
import br.com.circulou.circulou_backend.mapper.JornadaMapper;
import br.com.circulou.circulou_backend.model.Jornada;
import br.com.circulou.circulou_backend.model.JornadaTipo;
import br.com.circulou.circulou_backend.model.Usuario;
import br.com.circulou.circulou_backend.port.in.JornadaUseCase;
import br.com.circulou.circulou_backend.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaFacadeImplTest {

    @Mock
    private JornadaUseCase jornadaUseCase;

    @Mock
    private JornadaMapper jornadaMapper;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JornadaFacadeImpl facade;

    private final Long jornadaId = 1L;
    private final Long usuarioId = 10L;
    private final String userEmail = "test@circulou.com";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userEmail);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(userEmail);
        when(usuarioService.buscarPorEmail(userEmail)).thenReturn(usuario);
    }

    @Test
    @DisplayName("Deve buscar jornada própria com sucesso")
    void deveBuscarJornadaPropria() {
        Jornada jornada = new Jornada(usuarioId, JornadaTipo.PROPRIA);
        JornadaResponseDTO dto = new JornadaResponseDTO();

        when(jornadaUseCase.buscarPorId(jornadaId)).thenReturn(jornada);
        when(jornadaMapper.toResponseDTO(jornada)).thenReturn(dto);

        JornadaResponseDTO result = facade.buscarMeuPorId(jornadaId);

        assertNotNull(result);
        assertEquals(dto, result);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao buscar jornada de outro usuário")
    void deveLancarExceptionAoBuscarJornadaAlheia() {
        Jornada jornadaAlheia = new Jornada(99L, JornadaTipo.PROPRIA);
        when(jornadaUseCase.buscarPorId(jornadaId)).thenReturn(jornadaAlheia);

        assertThrows(AccessDeniedException.class, () -> facade.buscarMeuPorId(jornadaId));
        verify(jornadaMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Deve permitir mutação quando ownership for válido")
    void devePermitirMutacaoComOwnershipValido() {
        Jornada jornada = new Jornada(usuarioId, JornadaTipo.PROPRIA);
        when(jornadaUseCase.buscarPorId(jornadaId)).thenReturn(jornada);

        facade.iniciar(jornadaId);

        verify(jornadaUseCase).iniciar(jornadaId);
    }

    @Test
    @DisplayName("Deve bloquear mutação quando ownership for inválido")
    void deveBloquearMutacaoComOwnershipInvalido() {
        Jornada jornadaAlheia = new Jornada(99L, JornadaTipo.PROPRIA);
        when(jornadaUseCase.buscarPorId(jornadaId)).thenReturn(jornadaAlheia);

        assertThrows(AccessDeniedException.class, () -> facade.iniciar(jornadaId));
        verify(jornadaUseCase, never()).iniciar(anyLong());
    }
}
