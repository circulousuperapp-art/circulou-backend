package br.com.circulou.circulou_backend.port.in;

import br.com.circulou.circulou_backend.dto.ProdutoRequestDTO;
import br.com.circulou.circulou_backend.dto.ProdutoResponseDTO;
import java.util.List;

public interface ProdutoUseCase {
    List<ProdutoResponseDTO> listarTodos();
    ProdutoResponseDTO buscarPorId(Long id);
    ProdutoResponseDTO salvar(ProdutoRequestDTO dto);
    ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto);
    void deletar(Long id);
}
