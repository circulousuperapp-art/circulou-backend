package br.com.circulou.circulou_backend.adapter.out.persistence;

import br.com.circulou.circulou_backend.dto.OfertaFilterDTO;
import br.com.circulou.circulou_backend.model.Oferta;
import br.com.circulou.circulou_backend.port.out.OfertaRepositoryPort;
import br.com.circulou.circulou_backend.repository.OfertaJpaRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class OfertaRepositoryAdapter implements OfertaRepositoryPort {

    private final OfertaJpaRepository jpaRepository;

    public OfertaRepositoryAdapter(OfertaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Oferta> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<Oferta> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<Oferta> findWithFilters(OfertaFilterDTO filters, Pageable pageable) {
        Specification<Oferta> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getLojaId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("loja").get("id"), filters.getLojaId()));
            }

            if (filters.getCategoria() != null && !filters.getCategoria().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("produto").get("categoria"), filters.getCategoria()));
            }

            if (filters.getBusca() != null && !filters.getBusca().isBlank()) {
                String searchPattern = "%" + filters.getBusca().toLowerCase() + "%";
                Predicate nomePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("produto").get("nome")), searchPattern);
                Predicate marcaPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("produto").get("marca")), searchPattern);
                predicates.add(criteriaBuilder.or(nomePredicate, marcaPredicate));
            }

            if (Boolean.TRUE.equals(filters.getApenasDisponiveis())) {
                predicates.add(criteriaBuilder.equal(root.get("disponivel"), true));
                predicates.add(criteriaBuilder.greaterThan(root.get("estoque"), 0));
                predicates.add(criteriaBuilder.equal(root.get("ativo"), true));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return jpaRepository.findAll(spec, pageable);
    }

    @Override
    public List<Oferta> findAllById(Iterable<Long> ids) {
        return jpaRepository.findAllById(ids);
    }

    @Override
    public Optional<Oferta> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Oferta> findByLojaId(Long lojaId) {
        return jpaRepository.findByLojaId(lojaId);
    }

    @Override
    public List<Oferta> findByProdutoId(Long produtoId) {
        return jpaRepository.findByProdutoId(produtoId);
    }

    @Override
    public Optional<Oferta> findByLojaIdAndProdutoId(Long lojaId, Long produtoId) {
        return jpaRepository.findByLojaIdAndProdutoId(lojaId, produtoId);
    }

    @Override
    public Oferta save(Oferta oferta) {
        return jpaRepository.save(oferta);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
