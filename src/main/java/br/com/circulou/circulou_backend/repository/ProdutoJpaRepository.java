package br.com.circulou.circulou_backend.repository;

import br.com.circulou.circulou_backend.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoJpaRepository extends JpaRepository<Produto, Long> {

}