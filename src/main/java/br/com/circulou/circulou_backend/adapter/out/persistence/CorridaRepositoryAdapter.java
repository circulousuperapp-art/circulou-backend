package br.com.circulou.circulou_backend.adapter.out.persistence;

import br.com.circulou.circulou_backend.model.Corrida;
import br.com.circulou.circulou_backend.port.out.CorridaRepositoryPort;
import br.com.circulou.circulou_backend.repository.CorridaJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CorridaRepositoryAdapter implements CorridaRepositoryPort {

    private final CorridaJpaRepository jpaRepository;

    public CorridaRepositoryAdapter(CorridaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Corrida> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<Corrida> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Optional<Corrida> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Corrida> findByClienteId(Long clienteId) {
        return jpaRepository.findByClienteId(clienteId);
    }

    @Override
    public List<Corrida> findByMotoristaId(Long motoristaId) {
        return jpaRepository.findByMotoristaId(motoristaId);
    }

    @Override
    public Corrida save(Corrida corrida) {
        return jpaRepository.save(corrida);
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
