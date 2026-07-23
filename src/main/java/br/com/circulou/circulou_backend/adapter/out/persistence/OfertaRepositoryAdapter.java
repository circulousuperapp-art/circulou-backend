package br.com.circulou.circulou_backend.adapter.out.persistence;

import br.com.circulou.circulou_backend.model.Oferta;
import br.com.circulou.circulou_backend.port.out.OfertaRepositoryPort;
import br.com.circulou.circulou_backend.repository.OfertaJpaRepository;
import org.springframework.stereotype.Component;

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
