package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.dto.MotoristaProfileRequestDTO;
import br.com.circulou.circulou_backend.dto.MotoristaProfileResponseDTO;
import br.com.circulou.circulou_backend.exception.BusinessException;
import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.mapper.MotoristaProfileMapper;
import br.com.circulou.circulou_backend.model.MotoristaProfile;
import br.com.circulou.circulou_backend.model.StatusDocumentacao;
import br.com.circulou.circulou_backend.model.StatusPerfil;
import br.com.circulou.circulou_backend.model.Usuario;
import br.com.circulou.circulou_backend.port.in.MotoristaProfileUseCase;
import br.com.circulou.circulou_backend.port.out.MotoristaProfileRepositoryPort;
import br.com.circulou.circulou_backend.port.out.UsuarioRepositoryPort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MotoristaProfileService implements MotoristaProfileUseCase {

    private final MotoristaProfileRepositoryPort motoristaProfileRepositoryPort;
    private final MotoristaProfileMapper motoristaProfileMapper;
    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public MotoristaProfileService(MotoristaProfileRepositoryPort motoristaProfileRepositoryPort,
                                   MotoristaProfileMapper motoristaProfileMapper,
                                   UsuarioRepositoryPort usuarioRepositoryPort) {
        this.motoristaProfileRepositoryPort = motoristaProfileRepositoryPort;
        this.motoristaProfileMapper = motoristaProfileMapper;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public List<MotoristaProfileResponseDTO> listarTodos() {
        return motoristaProfileRepositoryPort.findAll()
                .stream()
                .map(motoristaProfileMapper::toResponseDTO)
                .toList();
    }

    @Override
    public MotoristaProfileResponseDTO buscarPorId(Long id) {
        MotoristaProfile entity = motoristaProfileRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de motorista não encontrado"));
        return motoristaProfileMapper.toResponseDTO(entity);
    }

    @Override
    @Transactional
    public MotoristaProfileResponseDTO salvar(MotoristaProfileRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            throw new BusinessException("Não é possível ativar perfil para um usuário inativo");
        }

        if (usuario.getMotoristaProfile() != null) {
            throw new BusinessException("Usuário já possui perfil de motorista");
        }

        MotoristaProfile entity = motoristaProfileMapper.toEntity(dto);
        
        // Inicialização centralizada de domínio
        entity.setUsuario(usuario);
        entity.setStatusPerfil(StatusPerfil.EM_ANALISE);
        entity.setStatusDocumentacao(StatusDocumentacao.PENDENTE);
        entity.setRatingMedia(BigDecimal.ZERO);

        // Garantindo integridade bidirecional do grafo
        usuario.setMotoristaProfile(entity);

        MotoristaProfile savedEntity = motoristaProfileRepositoryPort.save(entity);
        return motoristaProfileMapper.toResponseDTO(savedEntity);
    }

    @Override
    @Transactional
    public MotoristaProfileResponseDTO atualizar(Long id, MotoristaProfileRequestDTO dto) {
        MotoristaProfile entity = motoristaProfileRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de motorista não encontrado"));

        entity.setCnh(dto.getCnh());
        entity.setCategoriaCnh(dto.getCategoriaCnh());

        MotoristaProfile updatedEntity = motoristaProfileRepositoryPort.save(entity);
        return motoristaProfileMapper.toResponseDTO(updatedEntity);
    }

    @Override
    @Transactional
    public void desativar(Long id) {
        MotoristaProfile entity = motoristaProfileRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de motorista não encontrado"));

        entity.setStatusPerfil(StatusPerfil.INATIVO);
        motoristaProfileRepositoryPort.save(entity);
    }
}
