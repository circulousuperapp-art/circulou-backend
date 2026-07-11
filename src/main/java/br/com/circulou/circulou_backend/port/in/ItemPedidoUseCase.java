package br.com.circulou.circulou_backend.port.in;

import br.com.circulou.circulou_backend.dto.ItemPedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.ItemPedidoResponseDTO;
import java.util.List;

public interface ItemPedidoUseCase {
    List<ItemPedidoResponseDTO> listarTodos();
    ItemPedidoResponseDTO buscarPorId(Long id);
    ItemPedidoResponseDTO salvar(ItemPedidoRequestDTO dto);
    ItemPedidoResponseDTO atualizar(Long id, ItemPedidoRequestDTO dto);
    void deletar(Long id);
}
