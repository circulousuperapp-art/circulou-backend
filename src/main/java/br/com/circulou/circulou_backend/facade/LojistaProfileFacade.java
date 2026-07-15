package br.com.circulou.circulou_backend.facade;

import br.com.circulou.circulou_backend.dto.LojistaProfileRequestDTO;
import br.com.circulou.circulou_backend.dto.LojistaProfileResponseDTO;
import java.util.List;

public interface LojistaProfileFacade {
    List<LojistaProfileResponseDTO> listarTodos();
    LojistaProfileResponseDTO buscarPorId(Long id);
    LojistaProfileResponseDTO salvar(LojistaProfileRequestDTO dto);
    LojistaProfileResponseDTO atualizar(Long id, LojistaProfileRequestDTO dto);
    void desativar(Long id);
}
