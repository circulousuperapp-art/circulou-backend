package br.com.circulou.circulou_backend.port.out;

import br.com.circulou.circulou_backend.model.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoRepositoryPort {
    List<Pedido> findAll();
    Optional<Pedido> findById(Long id);
    Pedido save(Pedido pedido);
    void deleteById(Long id);
    boolean existsById(Long id);
}
