package br.com.circulou.circulou_backend.port.out;

import br.com.circulou.circulou_backend.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByEmail(String email);
    Usuario save(Usuario usuario);
    void deleteById(Long id);
    boolean existsById(Long id);
}
