package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.PedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.PedidoResponseDTO;
import br.com.circulou.circulou_backend.model.Pedido;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {

    public PedidoResponseDTO toResponseDTO(Pedido pedido) {
        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.getDataCriacao(),
                pedido.getUsuario() != null ? pedido.getUsuario().getId() : null,
                pedido.getLoja() != null ? pedido.getLoja().getId() : null
        );
    }

    public Pedido toEntity(PedidoRequestDTO dto) {
        Pedido pedido = new Pedido();
        updateEntityFromDto(pedido, dto);
        return pedido;
    }

    public void updateEntityFromDto(Pedido pedido, PedidoRequestDTO dto) {
        pedido.setValorTotal(dto.getValorTotal());
        pedido.setStatus(dto.getStatus());
        pedido.setDataCriacao(dto.getDataCriacao());
    }
}
