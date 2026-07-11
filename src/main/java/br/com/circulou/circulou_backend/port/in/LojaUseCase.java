package br.com.circulou.circulou_backend.port.in;

import br.com.circulou.circulou_backend.dto.LojaRequestDTO;
import br.com.circulou.circulou_backend.dto.LojaResponseDTO;
import java.util.List;

public interface LojaUseCase {
    List<LojaResponseDTO> listarTodos();
    LojaResponseDTO buscarPorId(Long id);
    LojaResponseDTO salvar(LojaRequestDTO dto);
    LojaResponseDTO atualizar(Long id, LojaRequestDTO dto);
    void deletar(Long id);
}
