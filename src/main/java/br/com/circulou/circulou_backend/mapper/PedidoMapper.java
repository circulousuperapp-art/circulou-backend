package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.PedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.PedidoResponseDTO;
import br.com.circulou.circulou_backend.model.Pedido;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PedidoMapper {

    private final ItemPedidoMapper itemPedidoMapper;

    public PedidoMapper(ItemPedidoMapper itemPedidoMapper) {
        this.itemPedidoMapper = itemPedidoMapper;
    }

    public PedidoResponseDTO toResponseDTO(Pedido pedido) {
        if (pedido == null) return null;

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.getDataCriacao(),
                pedido.getUsuario() != null ? pedido.getUsuario().getId() : null,
                pedido.getLoja() != null ? pedido.getLoja().getId() : null,
                pedido.getItens() != null ? 
                    pedido.getItens().stream().map(itemPedidoMapper::toResponseDTO).toList() : 
                    Collections.emptyList()
        );
    }

    public Pedido toEntity(PedidoRequestDTO dto) {
        if (dto == null) return null;
        
        Pedido pedido = new Pedido();
        // Na criação via Facade, os relacionamentos e itens serão tratados especificamente
        return pedido;
    }

    public void updateEntityFromDto(Pedido pedido, PedidoRequestDTO dto) {
        if (dto == null) return;
        // Campos que podem ser atualizados via DTO se necessário
    }
}
