package br.com.circulou.circulou_backend.adapter.out.persistence;

import br.com.circulou.circulou_backend.model.Jornada;
import br.com.circulou.circulou_backend.port.out.JornadaRepositoryPort;
import br.com.circulou.circulou_backend.repository.JornadaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JornadaRepositoryAdapter implements JornadaRepositoryPort {

    private final JornadaJpaRepository jpaRepository;

    public JornadaRepositoryAdapter(JornadaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Jornada save(Jornada jornada) {
        return jpaRepository.save(jornada);
    }

    @Override
    public Optional<Jornada> findById(Long id) {
        return jpaRepository.findById(id);
    }
}
