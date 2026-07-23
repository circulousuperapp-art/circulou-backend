package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.exception.BusinessException;
import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.*;
import br.com.circulou.circulou_backend.port.out.EventPublisherPort;
import br.com.circulou.circulou_backend.port.out.PedidoRepositoryPort;
import br.com.circulou.circulou_backend.service.OfertaService;
import br.com.circulou.circulou_backend.service.PedidoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final OfertaService ofertaService;
    private final PedidoStatusPolicy statusPolicy;
    private final EventPublisherPort eventPublisher;

    @Value("${circulou.pedido.janela-cancelamento-minutos:1}")
    private Integer janelaCancelamentoMinutos;

    public PedidoServiceImpl(PedidoRepositoryPort pedidoRepositoryPort,
                             OfertaService ofertaService,
                             PedidoStatusPolicy statusPolicy,
                             EventPublisherPort eventPublisher) {
        this.pedidoRepositoryPort = pedidoRepositoryPort;
        this.ofertaService = ofertaService;
        this.statusPolicy = statusPolicy;
        this.eventPublisher = eventPublisher;
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
        pedido.setDataLimiteCancelamento(LocalDateTime.now().plus(Duration.ofMinutes(janelaCancelamentoMinutos)));

        validarPedido(pedido);
        
        // Calcular totais
        pedido.getItens().forEach(ItemPedido::calcularSubtotal);
        pedido.calcularTotal();

        // Processar estoque
        processarEstoque(pedido);

        // Salva primeiro para garantir a geração do ID antes de transitar o status e gerar eventos
        Pedido pedidoSalvo = pedidoRepositoryPort.save(pedido);

        // Transição para o status inicial (gera PedidoCriadoEvent com o ID agora presente)
        pedidoSalvo.transitarPara(PedidoStatus.AGUARDANDO_LIBERACAO, statusPolicy);

        // Publicar eventos capturados
        eventPublisher.publish(pedidoSalvo.pullDomainEvents());

        return pedidoSalvo;
    }

    private void validarPedido(Pedido pedido) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new BusinessException("O pedido deve conter pelo menos um item");
        }

        if (pedido.getLoja() == null || !Boolean.TRUE.equals(pedido.getLoja().getAtiva())) {
            throw new BusinessException("A loja do pedido deve estar ativa");
        }

        for (ItemPedido item : pedido.getItens()) {
            ofertaService.validarParaVenda(
                    item.getOferta().getId(),
                    pedido.getLoja().getId(),
                    item.getQuantidade()
            );
        }
    }

    private void processarEstoque(Pedido pedido) {
        for (ItemPedido item : pedido.getItens()) {
            ofertaService.registrarVenda(item.getOferta().getId(), item.getQuantidade());
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

    @Override
    @Transactional
    public void alterarStatus(Long id, PedidoStatus novoStatus) {
        Pedido pedido = buscarEntidadePorId(id);
        pedido.transitarPara(novoStatus, statusPolicy);
        pedidoRepositoryPort.save(pedido);
        eventPublisher.publish(pedido.pullDomainEvents());
    }

    @Override
    @Transactional
    public void cancelar(Long id) {
        Pedido pedido = buscarEntidadePorId(id);
        pedido.cancelar(statusPolicy);
        pedidoRepositoryPort.save(pedido);
        eventPublisher.publish(pedido.pullDomainEvents());
    }

    private Pedido buscarEntidadePorId(Long id) {
        return pedidoRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
    }
}
