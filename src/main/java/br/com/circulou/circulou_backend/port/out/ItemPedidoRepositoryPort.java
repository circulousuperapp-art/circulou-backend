package br.com.circulou.circulou_backend.port.out;

import br.com.circulou.circulou_backend.model.ItemPedido;
import java.util.List;
import java.util.Optional;

public interface ItemPedidoRepositoryPort {
    List<ItemPedido> findAll();
    Optional<ItemPedido> findById(Long id);
    ItemPedido save(ItemPedido itemPedido);
    void deleteById(Long id);
    boolean existsById(Long id);
}
