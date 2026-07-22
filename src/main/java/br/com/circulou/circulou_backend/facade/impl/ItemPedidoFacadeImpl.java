package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.ItemPedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.ItemPedidoResponseDTO;
import br.com.circulou.circulou_backend.mapper.ItemPedidoMapper;
import br.com.circulou.circulou_backend.model.ItemPedido;
import br.com.circulou.circulou_backend.model.Pedido;
import br.com.circulou.circulou_backend.model.Oferta;
import br.com.circulou.circulou_backend.port.in.ItemPedidoUseCase;
import br.com.circulou.circulou_backend.service.ItemPedidoService;
import br.com.circulou.circulou_backend.service.PedidoService;
import br.com.circulou.circulou_backend.service.OfertaService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class ItemPedidoFacadeImpl implements ItemPedidoUseCase {

    private final ItemPedidoService itemPedidoService;
    private final PedidoService pedidoService;
    private final OfertaService ofertaService;
    private final ItemPedidoMapper itemPedidoMapper;

    public ItemPedidoFacadeImpl(ItemPedidoService itemPedidoService, 
                                 PedidoService pedidoService, 
                                 OfertaService ofertaService,
                                 ItemPedidoMapper itemPedidoMapper) {
        this.itemPedidoService = itemPedidoService;
        this.pedidoService = pedidoService;
        this.ofertaService = ofertaService;
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
    @Transactional
    public ItemPedidoResponseDTO salvar(ItemPedidoRequestDTO dto) {
        ItemPedido itemPedido = itemPedidoMapper.toEntity(dto);
        vincularRelacionamentos(itemPedido, dto.getPedidoId(), dto.getOfertaId());

        ItemPedido itemPedidoSalvo = itemPedidoService.salvar(itemPedido);
        return itemPedidoMapper.toResponseDTO(itemPedidoSalvo);
    }

    @Override
    @Transactional
    public ItemPedidoResponseDTO atualizar(Long id, ItemPedidoRequestDTO dto) {
        ItemPedido itemPedido = itemPedidoService.buscarPorId(id);

        itemPedidoMapper.updateEntityFromDto(itemPedido, dto);
        vincularRelacionamentos(itemPedido, dto.getPedidoId(), dto.getOfertaId());

        ItemPedido itemPedidoAtualizado = itemPedidoService.atualizar(id, itemPedido);
        return itemPedidoMapper.toResponseDTO(itemPedidoAtualizado);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        itemPedidoService.deletar(id);
    }

    private void vincularRelacionamentos(ItemPedido itemPedido, Long pedidoId, Long ofertaId) {
        if (pedidoId != null) {
            Pedido pedido = pedidoService.buscarPorId(pedidoId);
            itemPedido.setPedido(pedido);
        }

        if (ofertaId != null) {
            Oferta oferta = ofertaService.buscarPorId(ofertaId);
            itemPedido.setOferta(oferta);
        }
    }
}
