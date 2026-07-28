package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.Usuario;
import br.com.circulou.circulou_backend.port.out.MetricsPort;
import br.com.circulou.circulou_backend.port.out.UsuarioRepositoryPort;
import br.com.circulou.circulou_backend.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final MetricsPort metricsPort;

    public UsuarioServiceImpl(UsuarioRepositoryPort usuarioRepositoryPort, MetricsPort metricsPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.metricsPort = metricsPort;
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepositoryPort.findAll();
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return buscarEntidadePorId(id);
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o e-mail: " + email));
    }

    @Override
    @Transactional
    public Usuario salvar(Usuario usuario) {
        Usuario salvo = usuarioRepositoryPort.save(usuario);
        metricsPort.incrementarUsuariosCadastrados();
        return salvo;
    }

    @Override
    @Transactional
    public Usuario atualizar(Long id, Usuario usuario) {
        buscarEntidadePorId(id);
        usuario.setId(id);
        return usuarioRepositoryPort.save(usuario);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!usuarioRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        usuarioRepositoryPort.deleteById(id);
    }

    private Usuario buscarEntidadePorId(Long id) {
        return usuarioRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }
}
