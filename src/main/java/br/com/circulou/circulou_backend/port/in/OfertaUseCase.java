package br.com.circulou.circulou_backend.port.in;

import br.com.circulou.circulou_backend.dto.OfertaRequestDTO;
import br.com.circulou.circulou_backend.dto.OfertaResponseDTO;

import java.util.List;

public interface OfertaUseCase {
    List<OfertaResponseDTO> listarTodas();
    OfertaResponseDTO buscarPorId(Long id);
    List<OfertaResponseDTO> listarPorLoja(Long lojaId);
    List<OfertaResponseDTO> listarPorProduto(Long produtoId);
    OfertaResponseDTO salvar(OfertaRequestDTO dto);
    OfertaResponseDTO atualizar(Long id, OfertaRequestDTO dto);
    void deletar(Long id);
}
