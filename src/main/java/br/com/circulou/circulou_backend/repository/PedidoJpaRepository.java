package br.com.circulou.circulou_backend.repository;

import br.com.circulou.circulou_backend.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoJpaRepository extends JpaRepository<Pedido, Long> {
}
