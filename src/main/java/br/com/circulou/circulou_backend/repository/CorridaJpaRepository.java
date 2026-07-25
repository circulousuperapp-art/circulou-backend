package br.com.circulou.circulou_backend.repository;

import br.com.circulou.circulou_backend.model.Corrida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorridaJpaRepository extends JpaRepository<Corrida, Long> {
    List<Corrida> findByClienteId(Long clienteId);
    List<Corrida> findByMotoristaId(Long motoristaId);
}
