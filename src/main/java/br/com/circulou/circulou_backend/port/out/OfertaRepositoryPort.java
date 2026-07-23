package br.com.circulou.circulou_backend.port.out;

import br.com.circulou.circulou_backend.model.Oferta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OfertaRepositoryPort {
    List<Oferta> findAll();
    Page<Oferta> findAll(Pageable pageable);
    List<Oferta> findAllById(Iterable<Long> ids);
    Optional<Oferta> findById(Long id);
    List<Oferta> findByLojaId(Long lojaId);
    List<Oferta> findByProdutoId(Long produtoId);
    Optional<Oferta> findByLojaIdAndProdutoId(Long lojaId, Long produtoId);
    Oferta save(Oferta oferta);
    void deleteById(Long id);
    boolean existsById(Long id);
}
