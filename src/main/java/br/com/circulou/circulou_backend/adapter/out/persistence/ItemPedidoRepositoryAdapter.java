package br.com.circulou.circulou_backend.adapter.out.persistence;

import br.com.circulou.circulou_backend.model.ItemPedido;
import br.com.circulou.circulou_backend.port.out.ItemPedidoRepositoryPort;
import br.com.circulou.circulou_backend.repository.ItemPedidoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ItemPedidoRepositoryAdapter implements ItemPedidoRepositoryPort {

    private final ItemPedidoRepository itemPedidoRepository;

    public ItemPedidoRepositoryAdapter(ItemPedidoRepository itemPedidoRepository) {
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @Override
    public List<ItemPedido> findAll() {
        return itemPedidoRepository.findAll();
    }

    @Override
    public Optional<ItemPedido> findById(Long id) {
        return itemPedidoRepository.findById(id);
    }

    @Override
    public ItemPedido save(ItemPedido itemPedido) {
        return itemPedidoRepository.save(itemPedido);
    }

    @Override
    public void deleteById(Long id) {
        itemPedidoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return itemPedidoRepository.existsById(id);
    }
}
