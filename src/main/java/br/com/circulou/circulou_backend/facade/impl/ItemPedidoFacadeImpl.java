package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.ItemPedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.ItemPedidoResponseDTO;
import br.com.circulou.circulou_backend.mapper.ItemPedidoMapper;
import br.com.circulou.circulou_backend.model.ItemPedido;
import br.com.circulou.circulou_backend.model.Pedido;
import br.com.circulou.circulou_backend.model.Produto;
import br.com.circulou.circulou_backend.port.in.ItemPedidoUseCase;
import br.com.circulou.circulou_backend.service.ItemPedidoService;
import br.com.circulou.circulou_backend.service.PedidoService;
import br.com.circulou.circulou_backend.service.ProdutoService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemPedidoFacadeImpl implements ItemPedidoUseCase {

    private final ItemPedidoService itemPedidoService;
    private final PedidoService pedidoService;
    private final ProdutoService produtoService;
    private final ItemPedidoMapper itemPedidoMapper;

    public ItemPedidoFacadeImpl(ItemPedidoService itemPedidoService, 
                                 PedidoService pedidoService, 
                                 ProdutoService produtoService,
                                 ItemPedidoMapper itemPedidoMapper) {
        this.itemPedidoService = itemPedidoService;
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;
        this.itemPedidoMapper = itemPedidoMapper;
    }

    @Override
    public List<ItemPedidoResponseDTO> listarTodos() {
        return itemPedidoService.listarTodos()
                .stream()
                .map(itemPedidoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public ItemPedidoResponseDTO buscarPorId(Long id) {
        ItemPedido itemPedido = itemPedidoService.buscarPorId(id);
        return itemPedidoMapper.toResponseDTO(itemPedido);
    }

    @Override
    public ItemPedidoResponseDTO salvar(ItemPedidoRequestDTO dto) {
        ItemPedido itemPedido = itemPedidoMapper.toEntity(dto);
        vincularRelacionamentos(itemPedido, dto.getPedidoId(), dto.getProdutoId());

        ItemPedido itemPedidoSalvo = itemPedidoService.salvar(itemPedido);
        return itemPedidoMapper.toResponseDTO(itemPedidoSalvo);
    }

    @Override
    public ItemPedidoResponseDTO atualizar(Long id, ItemPedidoRequestDTO dto) {
        ItemPedido itemPedido = itemPedidoService.buscarPorId(id);

        itemPedidoMapper.updateEntityFromDto(itemPedido, dto);
        vincularRelacionamentos(itemPedido, dto.getPedidoId(), dto.getProdutoId());

        ItemPedido itemPedidoAtualizado = itemPedidoService.atualizar(id, itemPedido);
        return itemPedidoMapper.toResponseDTO(itemPedidoAtualizado);
    }

    @Override
    public void deletar(Long id) {
        itemPedidoService.deletar(id);
    }

    private void vincularRelacionamentos(ItemPedido itemPedido, Long pedidoId, Long produtoId) {
        if (pedidoId != null) {
            Pedido pedido = pedidoService.buscarPorId(pedidoId);
            itemPedido.setPedido(pedido);
        }

        if (produtoId != null) {
            Produto produto = produtoService.buscarPorId(produtoId);
            itemPedido.setProduto(produto);
        }
    }
}
