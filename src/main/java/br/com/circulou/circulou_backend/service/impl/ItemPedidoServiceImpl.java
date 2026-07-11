package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.dto.ItemPedidoRequestDTO;
import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.ItemPedido;
import br.com.circulou.circulou_backend.port.out.ItemPedidoRepositoryPort;
import br.com.circulou.circulou_backend.service.ItemPedidoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ItemPedidoServiceImpl implements ItemPedidoService {

    private final ItemPedidoRepositoryPort itemPedidoRepositoryPort;

    public ItemPedidoServiceImpl(ItemPedidoRepositoryPort itemPedidoRepositoryPort) {
        this.itemPedidoRepositoryPort = itemPedidoRepositoryPort;
    }

    @Override
    public List<ItemPedido> listarTodos() {
        return itemPedidoRepositoryPort.findAll();
    }

    @Override
    public ItemPedido buscarPorId(Long id) {
        return buscarEntidadePorId(id);
    }

    @Override
    @Transactional
    public ItemPedido salvar(ItemPedido itemPedido) {
        return itemPedidoRepositoryPort.save(itemPedido);
    }

    @Override
    @Transactional
    public ItemPedido atualizar(Long id, ItemPedido itemPedido) {
        buscarEntidadePorId(id);
        itemPedido.setId(id);
        return itemPedidoRepositoryPort.save(itemPedido);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!itemPedidoRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("ItemPedido não encontrado");
        }
        itemPedidoRepositoryPort.deleteById(id);
    }

    private ItemPedido buscarEntidadePorId(Long id) {
        return itemPedidoRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemPedido não encontrado"));
    }
}
