package br.com.circulou.circulou_backend.repository;

import br.com.circulou.circulou_backend.model.MotoristaProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotoristaProfileJpaRepository extends JpaRepository<MotoristaProfile, Long> {
}
