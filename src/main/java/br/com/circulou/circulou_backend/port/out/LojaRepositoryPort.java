package br.com.circulou.circulou_backend.port.out;

import br.com.circulou.circulou_backend.model.Loja;
import java.util.List;
import java.util.Optional;

public interface LojaRepositoryPort {
    List<Loja> findAll();
    Optional<Loja> findById(Long id);
    Loja save(Loja loja);
    void deleteById(Long id);
    boolean existsById(Long id);
}
