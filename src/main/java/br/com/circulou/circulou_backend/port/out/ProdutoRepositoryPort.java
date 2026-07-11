package br.com.circulou.circulou_backend.port.out;

import br.com.circulou.circulou_backend.model.Produto;
import java.util.List;
import java.util.Optional;

public interface ProdutoRepositoryPort {
    List<Produto> findAll();
    Optional<Produto> findById(Long id);
    Produto save(Produto produto);
    void deleteById(Long id);
    boolean existsById(Long id);
}
