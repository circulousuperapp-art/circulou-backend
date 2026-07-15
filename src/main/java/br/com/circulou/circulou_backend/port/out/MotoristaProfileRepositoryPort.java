package br.com.circulou.circulou_backend.port.out;

import br.com.circulou.circulou_backend.model.MotoristaProfile;
import java.util.List;
import java.util.Optional;

public interface MotoristaProfileRepositoryPort {
    List<MotoristaProfile> findAll();
    Optional<MotoristaProfile> findById(Long id);
    MotoristaProfile save(MotoristaProfile motoristaProfile);
    void deleteById(Long id);
    boolean existsById(Long id);
}
