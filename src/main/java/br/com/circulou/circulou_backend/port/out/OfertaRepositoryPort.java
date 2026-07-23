package br.com.circulou.circulou_backend.port.out;

import br.com.circulou.circulou_backend.model.Oferta;
import java.util.List;
import java.util.Optional;

public interface OfertaRepositoryPort {
    List<Oferta> findAll();
    List<Oferta> findAllById(Iterable<Long> ids);
    Optional<Oferta> findById(Long id);
    List<Oferta> findByLojaId(Long lojaId);
    List<Oferta> findByProdutoId(Long produtoId);
    Optional<Oferta> findByLojaIdAndProdutoId(Long lojaId, Long produtoId);
    Oferta save(Oferta oferta);
    void deleteById(Long id);
    boolean existsById(Long id);
}
