package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.LojaRequestDTO;
import br.com.circulou.circulou_backend.dto.LojaResponseDTO;
import br.com.circulou.circulou_backend.mapper.LojaMapper;
import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.model.LojistaProfile;
import br.com.circulou.circulou_backend.port.in.LojaUseCase;
import br.com.circulou.circulou_backend.port.out.LojistaProfileRepositoryPort;
import br.com.circulou.circulou_backend.service.LojaService;
import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LojaFacadeImpl implements LojaUseCase {
    private final LojaService lojaService;
    private final LojaMapper lojaMapper;
    private final LojistaProfileRepositoryPort lojistaProfileRepositoryPort;

    public LojaFacadeImpl(LojaService lojaService,
                          LojaMapper lojaMapper,
                          LojistaProfileRepositoryPort lojistaProfileRepositoryPort) {
        this.lojaService = lojaService;
        this.lojaMapper = lojaMapper;
        this.lojistaProfileRepositoryPort = lojistaProfileRepositoryPort;
    }

    @Override
    public List<LojaResponseDTO> listarTodos() {
        return lojaService.listarTodos()
                .stream()
                .map(lojaMapper::toResponseDTO)
                .toList();
    }

    @Override
    public LojaResponseDTO buscarPorId(Long id) {
        Loja loja = lojaService.buscarPorId(id);
        return lojaMapper.toResponseDTO(loja);
    }

    @Override
    public LojaResponseDTO salvar(LojaRequestDTO dto) {
        Loja loja = lojaMapper.toEntity(dto);
        
        vincularLojistaProfile(loja, dto.getLojistaProfileId());

        Loja lojaSalva = lojaService.salvar(loja);
        return lojaMapper.toResponseDTO(lojaSalva);
    }

    @Override
    public LojaResponseDTO atualizar(Long id, LojaRequestDTO dto) {
        Loja loja = lojaService.buscarPorId(id);

        lojaMapper.updateEntityFromDto(loja, dto);
        
        vincularLojistaProfile(loja, dto.getLojistaProfileId());

        Loja lojaAtualizada = lojaService.atualizar(id, loja);
        return lojaMapper.toResponseDTO(lojaAtualizada);
    }

    private void vincularLojistaProfile(Loja loja, Long lojistaProfileId) {
        if (lojistaProfileId != null) {
            LojistaProfile profile = lojistaProfileRepositoryPort.findById(lojistaProfileId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lojista Profile não encontrado"));
            loja.setLojistaProfile(profile);
        }
    }

    @Override
    public void deletar(Long id) {
        lojaService.deletar(id);
    }
}
