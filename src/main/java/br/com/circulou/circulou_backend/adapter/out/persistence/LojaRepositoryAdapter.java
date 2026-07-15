package br.com.circulou.circulou_backend.adapter.out.persistence;

import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.port.out.LojaRepositoryPort;
import br.com.circulou.circulou_backend.repository.LojaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LojaRepositoryAdapter implements LojaRepositoryPort {

    private final LojaJpaRepository lojaRepository;

    public LojaRepositoryAdapter(LojaJpaRepository lojaRepository) {
        this.lojaRepository = lojaRepository;
    }

    @Override
    public List<Loja> findAll() {
        return lojaRepository.findAll();
    }

    @Override
    public Optional<Loja> findById(Long id) {
        return lojaRepository.findById(id);
    }

    @Override
    public Loja save(Loja loja) {
        return lojaRepository.save(loja);
    }

    @Override
    public void deleteById(Long id) {
        lojaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return lojaRepository.existsById(id);
    }
}
