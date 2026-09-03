package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.JornadaResponseDTO;
import br.com.circulou.circulou_backend.exception.AccessDeniedException;
import br.com.circulou.circulou_backend.facade.JornadaFacade;
import br.com.circulou.circulou_backend.mapper.JornadaMapper;
import br.com.circulou.circulou_backend.model.Jornada;
import br.com.circulou.circulou_backend.model.Usuario;
import br.com.circulou.circulou_backend.port.in.JornadaUseCase;
import br.com.circulou.circulou_backend.service.UsuarioService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class JornadaFacadeImpl implements JornadaFacade {

    private final JornadaUseCase jornadaUseCase;
    private final JornadaMapper jornadaMapper;
    private final UsuarioService usuarioService;

    public JornadaFacadeImpl(JornadaUseCase jornadaUseCase,
                             JornadaMapper jornadaMapper,
                             UsuarioService usuarioService) {
        this.jornadaUseCase = jornadaUseCase;
        this.jornadaMapper = jornadaMapper;
        this.usuarioService = usuarioService;
    }

    @Override
    public JornadaResponseDTO buscarMeuPorId(Long id) {
        Jornada jornada = buscarValidandoOwnership(id);
        return jornadaMapper.toResponseDTO(jornada);
    }

    @Override
    @Transactional
    public void iniciar(Long id) {
        buscarValidandoOwnership(id);
        jornadaUseCase.iniciar(id);
    }

    @Override
    @Transactional
    public void iniciarDeslocamentoProximaParada(Long id) {
        buscarValidandoOwnership(id);
        jornadaUseCase.iniciarDeslocamentoProximaParada(id);
    }

    @Override
    @Transactional
    public void registrarChegadaNaLoja(Long id) {
        buscarValidandoOwnership(id);
        jornadaUseCase.registrarChegadaNaLoja(id);
    }

    @Override
    @Transactional
    public void confirmarRetirada(Long id) {
        buscarValidandoOwnership(id);
        jornadaUseCase.confirmarRetirada(id);
    }

    @Override
    @Transactional
    public void cancelarParada(Long id, Integer ordem) {
        buscarValidandoOwnership(id);
        jornadaUseCase.cancelarParada(id, ordem);
    }

    @Override
    @Transactional
    public void concluir(Long id) {
        buscarValidandoOwnership(id);
        jornadaUseCase.concluir(id);
    }

    @Override
    @Transactional
    public void cancelar(Long id) {
        buscarValidandoOwnership(id);
        jornadaUseCase.cancelar(id);
    }

    private Jornada buscarValidandoOwnership(Long id) {
        Usuario usuario = obterUsuarioAutenticado();
        Jornada jornada = jornadaUseCase.buscarPorId(id);

        if (!jornada.getClienteId().equals(usuario.getId())) {
            throw new AccessDeniedException("Acesso negado: Esta jornada não pertence ao seu usuário");
        }

        return jornada;
    }

    private Usuario obterUsuarioAutenticado() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return usuarioService.buscarPorEmail(email);
    }
}
