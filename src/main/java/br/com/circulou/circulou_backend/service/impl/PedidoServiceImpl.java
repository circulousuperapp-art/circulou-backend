package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.exception.BusinessException;
import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.ItemPedido;
import br.com.circulou.circulou_backend.model.Oferta;
import br.com.circulou.circulou_backend.model.Pedido;
import br.com.circulou.circulou_backend.port.out.PedidoRepositoryPort;
import br.com.circulou.circulou_backend.service.OfertaService;
import br.com.circulou.circulou_backend.service.PedidoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final OfertaService ofertaService;

    public PedidoServiceImpl(PedidoRepositoryPort pedidoRepositoryPort, OfertaService ofertaService) {
        this.pedidoRepositoryPort = pedidoRepositoryPort;
        this.ofertaService = ofertaService;
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
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setStatus("PENDENTE");

        validarPedido(pedido);
        
        // Calcular totais
        pedido.getItens().forEach(ItemPedido::calcularSubtotal);
        pedido.calcularTotal();

        // Processar estoque
        processarEstoque(pedido);

        return pedidoRepositoryPort.save(pedido);
    }

    private void validarPedido(Pedido pedido) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new BusinessException("O pedido deve conter pelo menos um item");
        }

        if (pedido.getLoja() == null || !Boolean.TRUE.equals(pedido.getLoja().getAtiva())) {
            throw new BusinessException("A loja do pedido deve estar ativa");
        }

        for (ItemPedido item : pedido.getItens()) {
            Oferta oferta = item.getOferta();
            
            if (oferta == null) {
                throw new BusinessException("Oferta não encontrada para um dos itens");
            }

            if (!Boolean.TRUE.equals(oferta.getAtivo()) || !Boolean.TRUE.equals(oferta.getDisponivel())) {
                throw new BusinessException("A oferta do produto " + item.getNomeProduto() + " não está disponível");
            }

            if (!Boolean.TRUE.equals(oferta.getProduto().getAtivo())) {
                throw new BusinessException("O produto " + item.getNomeProduto() + " está inativo");
            }

            if (!oferta.getLoja().getId().equals(pedido.getLoja().getId())) {
                throw new BusinessException("A oferta " + item.getNomeProduto() + " não pertence à loja do pedido");
            }

            if (oferta.getEstoque() < item.getQuantidade()) {
                throw new BusinessException("Estoque insuficiente para o produto " + item.getNomeProduto());
            }
        }
    }

    private void processarEstoque(Pedido pedido) {
        for (ItemPedido item : pedido.getItens()) {
            Oferta oferta = item.getOferta();
            oferta.setEstoque(oferta.getEstoque() - item.getQuantidade());
            
            // Se o estoque chegar a zero, podemos marcar como indisponível automaticamente?
            // Por enquanto apenas decrementamos conforme solicitado.
            ofertaService.atualizar(oferta.getId(), oferta);
        }
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
