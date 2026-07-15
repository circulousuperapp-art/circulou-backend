package br.com.circulou.circulou_backend.adapter.out.persistence;

import br.com.circulou.circulou_backend.model.MotoristaProfile;
import br.com.circulou.circulou_backend.port.out.MotoristaProfileRepositoryPort;
import br.com.circulou.circulou_backend.repository.MotoristaProfileJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MotoristaProfileRepositoryAdapter implements MotoristaProfileRepositoryPort {

    private final MotoristaProfileJpaRepository jpaRepository;

    public MotoristaProfileRepositoryAdapter(MotoristaProfileJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<MotoristaProfile> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<MotoristaProfile> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public MotoristaProfile save(MotoristaProfile motoristaProfile) {
        return jpaRepository.save(motoristaProfile);
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
