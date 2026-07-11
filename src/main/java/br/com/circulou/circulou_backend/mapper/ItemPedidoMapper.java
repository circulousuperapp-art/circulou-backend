package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.ItemPedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.ItemPedidoResponseDTO;
import br.com.circulou.circulou_backend.model.ItemPedido;
import org.springframework.stereotype.Component;

@Component
public class ItemPedidoMapper {

    public ItemPedidoResponseDTO toResponseDTO(ItemPedido itemPedido) {
        return new ItemPedidoResponseDTO(
                itemPedido.getId(),
                itemPedido.getQuantidade(),
                itemPedido.getPrecoUnitario(),
                itemPedido.getSubtotal(),
                itemPedido.getPedido() != null ? itemPedido.getPedido().getId() : null,
                itemPedido.getProduto() != null ? itemPedido.getProduto().getId() : null
        );
    }

    public ItemPedido toEntity(ItemPedidoRequestDTO dto) {
        ItemPedido itemPedido = new ItemPedido();
        updateEntityFromDto(itemPedido, dto);
        return itemPedido;
    }

    public void updateEntityFromDto(ItemPedido itemPedido, ItemPedidoRequestDTO dto) {
        itemPedido.setQuantidade(dto.getQuantidade());
        itemPedido.setPrecoUnitario(dto.getPrecoUnitario());
        itemPedido.setSubtotal(dto.getSubtotal());
    }
}
