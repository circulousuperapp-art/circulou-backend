package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.dto.LojistaProfileRequestDTO;
import br.com.circulou.circulou_backend.dto.LojistaProfileResponseDTO;
import br.com.circulou.circulou_backend.exception.BusinessException;
import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.mapper.LojistaProfileMapper;
import br.com.circulou.circulou_backend.model.LojistaProfile;
import br.com.circulou.circulou_backend.model.StatusDocumentacao;
import br.com.circulou.circulou_backend.model.StatusPerfil;
import br.com.circulou.circulou_backend.model.Usuario;
import br.com.circulou.circulou_backend.port.in.LojistaProfileUseCase;
import br.com.circulou.circulou_backend.port.out.LojistaProfileRepositoryPort;
import br.com.circulou.circulou_backend.port.out.UsuarioRepositoryPort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LojistaProfileService implements LojistaProfileUseCase {

    private final LojistaProfileRepositoryPort lojistaProfileRepositoryPort;
    private final LojistaProfileMapper lojistaProfileMapper;
    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public LojistaProfileService(LojistaProfileRepositoryPort lojistaProfileRepositoryPort,
                                 LojistaProfileMapper lojistaProfileMapper,
                                 UsuarioRepositoryPort usuarioRepositoryPort) {
        this.lojistaProfileRepositoryPort = lojistaProfileRepositoryPort;
        this.lojistaProfileMapper = lojistaProfileMapper;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public List<LojistaProfileResponseDTO> listarTodos() {
        return lojistaProfileRepositoryPort.findAll()
                .stream()
                .map(lojistaProfileMapper::toResponseDTO)
                .toList();
    }

    @Override
    public LojistaProfileResponseDTO buscarPorId(Long id) {
        LojistaProfile entity = lojistaProfileRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de lojista não encontrado"));
        return lojistaProfileMapper.toResponseDTO(entity);
    }

    @Override
    @Transactional
    public LojistaProfileResponseDTO salvar(LojistaProfileRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            throw new BusinessException("Não é possível ativar perfil para um usuário inativo");
        }

        if (usuario.getLojistaProfile() != null) {
            throw new BusinessException("Usuário já possui perfil de lojista");
        }

        LojistaProfile entity = lojistaProfileMapper.toEntity(dto);

        // Inicialização centralizada de domínio
        entity.setUsuario(usuario);
        entity.setStatusPerfil(StatusPerfil.EM_ANALISE);
        entity.setStatusDocumentacao(StatusDocumentacao.PENDENTE);
        entity.setRatingMedia(BigDecimal.ZERO);

        // Garantindo integridade bidirecional do grafo
        usuario.setLojistaProfile(entity);

        LojistaProfile savedEntity = lojistaProfileRepositoryPort.save(entity);
        return lojistaProfileMapper.toResponseDTO(savedEntity);
    }

    @Override
    @Transactional
    public LojistaProfileResponseDTO atualizar(Long id, LojistaProfileRequestDTO dto) {
        LojistaProfile entity = lojistaProfileRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de lojista não encontrado"));

        entity.setCnpj(dto.getCnpj());
        entity.setRazaoSocial(dto.getRazaoSocial());
        entity.setSegmento(dto.getSegmento());

        LojistaProfile updatedEntity = lojistaProfileRepositoryPort.save(entity);
        return lojistaProfileMapper.toResponseDTO(updatedEntity);
    }

    @Override
    @Transactional
    public void desativar(Long id) {
        LojistaProfile entity = lojistaProfileRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de lojista não encontrado"));

        entity.setStatusPerfil(StatusPerfil.INATIVO);
        lojistaProfileRepositoryPort.save(entity);
    }
}
