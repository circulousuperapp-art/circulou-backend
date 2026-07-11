package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.Pedido;
import br.com.circulou.circulou_backend.port.out.PedidoRepositoryPort;
import br.com.circulou.circulou_backend.service.PedidoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepositoryPort pedidoRepositoryPort;

    public PedidoServiceImpl(PedidoRepositoryPort pedidoRepositoryPort) {
        this.pedidoRepositoryPort = pedidoRepositoryPort;
    }

    @Override
    public List<Pedido> listarTodos() {
        return pedidoRepositoryPort.findAll();
    }

    @Override
    public Pedido buscarPorId(Long id) {
        return buscarEntidadePorId(id);
    }

    @Override
    @Transactional
    public Pedido salvar(Pedido pedido) {
        return pedidoRepositoryPort.save(pedido);
    }

    @Override
    @Transactional
    public Pedido atualizar(Long id, Pedido pedido) {
        buscarEntidadePorId(id);
        pedido.setId(id);
        return pedidoRepositoryPort.save(pedido);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!pedidoRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Pedido não encontrado");
        }
        pedidoRepositoryPort.deleteById(id);
    }

    private Pedido buscarEntidadePorId(Long id) {
        return pedidoRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
    }
}
