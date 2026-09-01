package br.com.circulou.circulou_backend.port.out;

import br.com.circulou.circulou_backend.model.Jornada;

import java.util.Optional;

public interface JornadaRepositoryPort {
    Jornada save(Jornada jornada);
    Optional<Jornada> findById(Long id);
}
