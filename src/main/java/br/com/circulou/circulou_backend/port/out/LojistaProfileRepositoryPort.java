package br.com.circulou.circulou_backend.port.out;

import br.com.circulou.circulou_backend.model.LojistaProfile;
import java.util.List;
import java.util.Optional;

public interface LojistaProfileRepositoryPort {
    List<LojistaProfile> findAll();
    Optional<LojistaProfile> findById(Long id);
    LojistaProfile save(LojistaProfile lojistaProfile);
    void deleteById(Long id);
    boolean existsById(Long id);
}
