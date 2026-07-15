package br.com.circulou.circulou_backend.repository;

import br.com.circulou.circulou_backend.model.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfertaJpaRepository extends JpaRepository<Oferta, Long> {
    List<Oferta> findByLojaId(Long lojaId);
    List<Oferta> findByProdutoId(Long produtoId);
    Optional<Oferta> findByLojaIdAndProdutoId(Long lojaId, Long produtoId);
}
