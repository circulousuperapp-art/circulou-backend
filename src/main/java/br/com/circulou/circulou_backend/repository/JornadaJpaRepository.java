package br.com.circulou.circulou_backend.repository;

import br.com.circulou.circulou_backend.model.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JornadaJpaRepository extends JpaRepository<Jornada, Long> {
}
