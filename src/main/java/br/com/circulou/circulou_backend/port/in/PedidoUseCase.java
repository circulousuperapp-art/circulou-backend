package br.com.circulou.circulou_backend.port.in;

import br.com.circulou.circulou_backend.dto.PedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.PedidoResponseDTO;
import java.util.List;

public interface PedidoUseCase {
    List<PedidoResponseDTO> listarTodos();
    PedidoResponseDTO buscarPorId(Long id);
    PedidoResponseDTO salvar(PedidoRequestDTO dto);
    PedidoResponseDTO atualizar(Long id, PedidoRequestDTO dto);
    void deletar(Long id);
}
