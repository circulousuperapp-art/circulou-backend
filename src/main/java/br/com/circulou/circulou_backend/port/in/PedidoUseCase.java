package br.com.circulou.circulou_backend.port.in;

import br.com.circulou.circulou_backend.dto.PedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.PedidoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PedidoUseCase {
    List<PedidoResponseDTO> listarTodos();
    Page<PedidoResponseDTO> listarTodos(Pageable pageable);
    List<PedidoResponseDTO> listarPorUsuario(Long usuarioId);
    Page<PedidoResponseDTO> listarPorUsuario(Long usuarioId, Pageable pageable);
    PedidoResponseDTO buscarPorId(Long id);
    Page<PedidoResponseDTO> listarMeusPedidos(Pageable pageable);
    PedidoResponseDTO buscarMeuPedidoPorId(Long id);
    PedidoResponseDTO salvar(PedidoRequestDTO dto);
    PedidoResponseDTO atualizar(Long id, PedidoRequestDTO dto);
    void deletar(Long id);
    void cancelar(Long id);
    void aceitar(Long id);
    void preparar(Long id);
    void marcarComoPronto(Long id);
    void finalizar(Long id);
}
