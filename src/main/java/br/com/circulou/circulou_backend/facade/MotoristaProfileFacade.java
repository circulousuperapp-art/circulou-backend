package br.com.circulou.circulou_backend.facade;

import br.com.circulou.circulou_backend.dto.MotoristaProfileRequestDTO;
import br.com.circulou.circulou_backend.dto.MotoristaProfileResponseDTO;
import java.util.List;

public interface MotoristaProfileFacade {
    List<MotoristaProfileResponseDTO> listarTodos();
    MotoristaProfileResponseDTO buscarPorId(Long id);
    MotoristaProfileResponseDTO salvar(MotoristaProfileRequestDTO dto);
    MotoristaProfileResponseDTO atualizar(Long id, MotoristaProfileRequestDTO dto);
    void desativar(Long id);
}
