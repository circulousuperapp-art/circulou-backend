package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.MotoristaProfileRequestDTO;
import br.com.circulou.circulou_backend.dto.MotoristaProfileResponseDTO;
import br.com.circulou.circulou_backend.facade.MotoristaProfileFacade;
import br.com.circulou.circulou_backend.port.in.MotoristaProfileUseCase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MotoristaProfileFacadeImpl implements MotoristaProfileFacade {

    private final MotoristaProfileUseCase motoristaProfileUseCase;

    public MotoristaProfileFacadeImpl(MotoristaProfileUseCase motoristaProfileUseCase) {
        this.motoristaProfileUseCase = motoristaProfileUseCase;
    }

    @Override
    public List<MotoristaProfileResponseDTO> listarTodos() {
        return motoristaProfileUseCase.listarTodos();
    }

    @Override
    public MotoristaProfileResponseDTO buscarPorId(Long id) {
        return motoristaProfileUseCase.buscarPorId(id);
    }

    @Override
    public MotoristaProfileResponseDTO salvar(MotoristaProfileRequestDTO dto) {
        return motoristaProfileUseCase.salvar(dto);
    }

    @Override
    public MotoristaProfileResponseDTO atualizar(Long id, MotoristaProfileRequestDTO dto) {
        return motoristaProfileUseCase.atualizar(id, dto);
    }

    @Override
    public void desativar(Long id) {
        motoristaProfileUseCase.desativar(id);
    }
}
