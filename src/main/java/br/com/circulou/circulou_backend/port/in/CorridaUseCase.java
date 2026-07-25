package br.com.circulou.circulou_backend.port.in;

import br.com.circulou.circulou_backend.dto.CorridaRequestDTO;
import br.com.circulou.circulou_backend.dto.CorridaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CorridaUseCase {
    List<CorridaResponseDTO> listarTodas();
    Page<CorridaResponseDTO> listarTodas(Pageable pageable);
    CorridaResponseDTO buscarPorId(Long id);
    List<CorridaResponseDTO> listarPorCliente(Long clienteId);
    List<CorridaResponseDTO> listarPorMotorista(Long motoristaId);
    CorridaResponseDTO solicitar(CorridaRequestDTO dto);
    CorridaResponseDTO atualizar(Long id, CorridaRequestDTO dto);
    void deletar(Long id);
    
    // Transições de Status
    void aceitar(Long id, Long motoristaId);
    void iniciarCaminho(Long id);
    void embarcar(Long id);
    void registrarParadaLoja(Long id);
    void continuarCorrida(Long id);
    void finalizar(Long id);
    void cancelar(Long id);
}
