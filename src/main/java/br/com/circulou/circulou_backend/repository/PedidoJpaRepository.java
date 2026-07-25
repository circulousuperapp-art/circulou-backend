package br.com.circulou.circulou_backend.repository;

import br.com.circulou.circulou_backend.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoJpaRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long usuarioId);
    Page<Pedido> findByUsuarioId(Long usuarioId, Pageable pageable);
}
