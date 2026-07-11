package br.com.circulou.circulou_backend.port.in;

import br.com.circulou.circulou_backend.dto.UsuarioRequestDTO;
import br.com.circulou.circulou_backend.dto.UsuarioResponseDTO;
import java.util.List;

public interface UsuarioUseCase {
    List<UsuarioResponseDTO> listarTodos();
    UsuarioResponseDTO buscarPorId(Long id);
    UsuarioResponseDTO salvar(UsuarioRequestDTO dto);
    UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto);
    void deletar(Long id);
}
