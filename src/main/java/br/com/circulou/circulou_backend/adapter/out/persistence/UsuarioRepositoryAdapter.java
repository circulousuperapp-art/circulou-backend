package br.com.circulou.circulou_backend.adapter.out.persistence;

import br.com.circulou.circulou_backend.model.Usuario;
import br.com.circulou.circulou_backend.port.out.UsuarioRepositoryPort;
import br.com.circulou.circulou_backend.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioRepository usuarioRepository;

    public UsuarioRepositoryAdapter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return usuarioRepository.existsById(id);
    }
}
