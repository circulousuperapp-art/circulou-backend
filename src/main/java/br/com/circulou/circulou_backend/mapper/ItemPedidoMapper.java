package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.ItemPedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.ItemPedidoResponseDTO;
import br.com.circulou.circulou_backend.dto.ItemPedidoSimplesDTO;
import br.com.circulou.circulou_backend.model.ItemPedido;
import org.springframework.stereotype.Component;

@Component
public class ItemPedidoMapper {

    public ItemPedidoResponseDTO toResponseDTO(ItemPedido itemPedido) {
        if (itemPedido == null) return null;

        return new ItemPedidoResponseDTO(
                itemPedido.getId(),
                itemPedido.getQuantidade(),
                itemPedido.getNomeProduto(),
                itemPedido.getPrecoUnitario(),
                itemPedido.getSubtotal(),
                itemPedido.getPedido() != null ? itemPedido.getPedido().getId() : null,
                itemPedido.getOferta() != null ? itemPedido.getOferta().getId() : null
        );
    }

    public ItemPedido toEntity(ItemPedidoSimplesDTO dto) {
        if (dto == null) return null;
        
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setQuantidade(dto.getQuantidade());
        return itemPedido;
    }

    public ItemPedido toEntity(ItemPedidoRequestDTO dto) {
        if (dto == null) return null;

        ItemPedido itemPedido = new ItemPedido();
        updateEntityFromDto(itemPedido, dto);
        return itemPedido;
    }

    public void updateEntityFromDto(ItemPedido itemPedido, ItemPedidoRequestDTO dto) {
        if (dto == null) return;
        itemPedido.setQuantidade(dto.getQuantidade());
    }
}
