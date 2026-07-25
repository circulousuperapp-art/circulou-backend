package br.com.circulou.circulou_backend.port.out;

import br.com.circulou.circulou_backend.model.Corrida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CorridaRepositoryPort {
    List<Corrida> findAll();
    Page<Corrida> findAll(Pageable pageable);
    Optional<Corrida> findById(Long id);
    List<Corrida> findByClienteId(Long clienteId);
    List<Corrida> findByMotoristaId(Long motoristaId);
    Corrida save(Corrida corrida);
    void deleteById(Long id);
    boolean existsById(Long id);
}
