package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.exception.BusinessException;
import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.ItemPedido;
import br.com.circulou.circulou_backend.model.Oferta;
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
        validarRegrasNegocio(itemPedido);
        registrarSnapshotComercial(itemPedido);
        return itemPedidoRepositoryPort.save(itemPedido);
    }

    @Override
    @Transactional
    public ItemPedido atualizar(Long id, ItemPedido itemPedido) {
        buscarEntidadePorId(id);
        validarRegrasNegocio(itemPedido);
        registrarSnapshotComercial(itemPedido);
        itemPedido.setId(id);
        return itemPedidoRepositoryPort.save(itemPedido);
    }

    private void validarRegrasNegocio(ItemPedido itemPedido) {
        Oferta oferta = itemPedido.getOferta();
        if (oferta == null) {
            throw new BusinessException("Oferta é obrigatória para o item do pedido");
        }

        if (Boolean.FALSE.equals(oferta.getAtivo())) {
            throw new BusinessException("Não é possível adicionar uma oferta inativa ao pedido");
        }

        if (Boolean.FALSE.equals(oferta.getDisponivel())) {
            throw new BusinessException("A oferta não está disponível no momento");
        }

        if (oferta.getLoja() == null || Boolean.FALSE.equals(oferta.getLoja().getAtiva())) {
            throw new BusinessException("A loja associada à oferta está inativa");
        }

        if (oferta.getProduto() == null || Boolean.FALSE.equals(oferta.getProduto().getAtivo())) {
            throw new BusinessException("O produto associado à oferta está inativo");
        }

        if (oferta.getEstoque() < itemPedido.getQuantidade()) {
            throw new BusinessException("Estoque insuficiente para a oferta: " + oferta.getProduto().getNome());
        }
    }

    private void registrarSnapshotComercial(ItemPedido itemPedido) {
        Oferta oferta = itemPedido.getOferta();
        itemPedido.setNomeProduto(oferta.getProduto().getNome());
        itemPedido.setPrecoUnitario(oferta.getPreco());
        itemPedido.calcularSubtotal();
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
