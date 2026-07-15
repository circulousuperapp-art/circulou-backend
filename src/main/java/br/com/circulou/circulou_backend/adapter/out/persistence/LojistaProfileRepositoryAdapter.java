package br.com.circulou.circulou_backend.adapter.out.persistence;

import br.com.circulou.circulou_backend.model.LojistaProfile;
import br.com.circulou.circulou_backend.port.out.LojistaProfileRepositoryPort;
import br.com.circulou.circulou_backend.repository.LojistaProfileJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LojistaProfileRepositoryAdapter implements LojistaProfileRepositoryPort {

    private final LojistaProfileJpaRepository jpaRepository;

    public LojistaProfileRepositoryAdapter(LojistaProfileJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<LojistaProfile> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<LojistaProfile> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public LojistaProfile save(LojistaProfile lojistaProfile) {
        return jpaRepository.save(lojistaProfile);
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
