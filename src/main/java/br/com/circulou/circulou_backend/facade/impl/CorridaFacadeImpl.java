package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.CorridaRequestDTO;
import br.com.circulou.circulou_backend.dto.CorridaResponseDTO;
import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.mapper.CorridaMapper;
import br.com.circulou.circulou_backend.model.Corrida;
import br.com.circulou.circulou_backend.model.CorridaStatus;
import br.com.circulou.circulou_backend.model.MotoristaProfile;
import br.com.circulou.circulou_backend.model.Usuario;
import br.com.circulou.circulou_backend.port.in.CorridaUseCase;
import br.com.circulou.circulou_backend.port.out.MotoristaProfileRepositoryPort;
import br.com.circulou.circulou_backend.port.out.UsuarioRepositoryPort;
import br.com.circulou.circulou_backend.service.CorridaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class CorridaFacadeImpl implements CorridaUseCase {

    private final CorridaService corridaService;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final MotoristaProfileRepositoryPort motoristaRepositoryPort;
    private final CorridaMapper corridaMapper;

    public CorridaFacadeImpl(CorridaService corridaService,
                             UsuarioRepositoryPort usuarioRepositoryPort,
                             MotoristaProfileRepositoryPort motoristaRepositoryPort,
                             CorridaMapper corridaMapper) {
        this.corridaService = corridaService;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.motoristaRepositoryPort = motoristaRepositoryPort;
        this.corridaMapper = corridaMapper;
    }

    @Override
    public List<CorridaResponseDTO> listarTodas() {
        return corridaService.listarTodas()
                .stream()
                .map(corridaMapper::toResponseDTO)
                .toList();
    }

    @Override
    public Page<CorridaResponseDTO> listarTodas(Pageable pageable) {
        return corridaService.listarTodas(pageable)
                .map(corridaMapper::toResponseDTO);
    }

    @Override
    public CorridaResponseDTO buscarPorId(Long id) {
        Corrida corrida = corridaService.buscarPorId(id);
        return corridaMapper.toResponseDTO(corrida);
    }

    @Override
    public List<CorridaResponseDTO> listarPorCliente(Long clienteId) {
        return corridaService.listarPorCliente(clienteId)
                .stream()
                .map(corridaMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<CorridaResponseDTO> listarPorMotorista(Long motoristaId) {
        return corridaService.listarPorMotorista(motoristaId)
                .stream()
                .map(corridaMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public CorridaResponseDTO solicitar(CorridaRequestDTO dto) {
        Corrida corrida = corridaMapper.toEntity(dto);
        Usuario cliente = usuarioRepositoryPort.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        corrida.setCliente(cliente);
        
        Corrida salva = corridaService.salvar(corrida);
        return corridaMapper.toResponseDTO(salva);
    }

    @Override
    @Transactional
    public CorridaResponseDTO atualizar(Long id, CorridaRequestDTO dto) {
        Corrida corrida = corridaService.buscarPorId(id);
        corridaMapper.updateEntityFromDto(corrida, dto);
        Corrida atualizada = corridaService.atualizar(id, corrida);
        return corridaMapper.toResponseDTO(atualizada);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        corridaService.deletar(id);
    }

    @Override
    @Transactional
    public void aceitar(Long id, Long motoristaId) {
        Corrida corrida = corridaService.buscarPorId(id);
        MotoristaProfile motorista = motoristaRepositoryPort.findById(motoristaId)
                .orElseThrow(() -> new ResourceNotFoundException("Motorista não encontrado"));
        corrida.setMotorista(motorista);
        corridaService.alterarStatus(id, CorridaStatus.ACEITA);
    }

    @Override
    @Transactional
    public void iniciarCaminho(Long id) {
        corridaService.alterarStatus(id, CorridaStatus.MOTORISTA_A_CAMINHO);
    }

    @Override
    @Transactional
    public void embarcar(Long id) {
        corridaService.alterarStatus(id, CorridaStatus.CLIENTE_EMBARCADO);
    }

    @Override
    @Transactional
    public void registrarParadaLoja(Long id) {
        corridaService.alterarStatus(id, CorridaStatus.PARADA_NA_LOJA);
    }

    @Override
    @Transactional
    public void continuarCorrida(Long id) {
        corridaService.alterarStatus(id, CorridaStatus.EM_DESLOCAMENTO);
    }

    @Override
    @Transactional
    public void finalizar(Long id) {
        corridaService.alterarStatus(id, CorridaStatus.FINALIZADA);
    }

    @Override
    @Transactional
    public void cancelar(Long id) {
        corridaService.alterarStatus(id, CorridaStatus.CANCELADA);
    }
}
