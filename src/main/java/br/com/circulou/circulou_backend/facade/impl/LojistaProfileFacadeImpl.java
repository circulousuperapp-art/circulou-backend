package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.LojistaProfileRequestDTO;
import br.com.circulou.circulou_backend.dto.LojistaProfileResponseDTO;
import br.com.circulou.circulou_backend.facade.LojistaProfileFacade;
import br.com.circulou.circulou_backend.port.in.LojistaProfileUseCase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class LojistaProfileFacadeImpl implements LojistaProfileFacade {

    private final LojistaProfileUseCase lojistaProfileUseCase;

    public LojistaProfileFacadeImpl(LojistaProfileUseCase lojistaProfileUseCase) {
        this.lojistaProfileUseCase = lojistaProfileUseCase;
    }

    @Override
    public List<LojistaProfileResponseDTO> listarTodos() {
        return lojistaProfileUseCase.listarTodos();
    }

    @Override
    public LojistaProfileResponseDTO buscarPorId(Long id) {
        return lojistaProfileUseCase.buscarPorId(id);
    }

    @Override
    @Transactional
    public LojistaProfileResponseDTO salvar(LojistaProfileRequestDTO dto) {
        return lojistaProfileUseCase.salvar(dto);
    }

    @Override
    @Transactional
    public LojistaProfileResponseDTO atualizar(Long id, LojistaProfileRequestDTO dto) {
        return lojistaProfileUseCase.atualizar(id, dto);
    }

    @Override
    @Transactional
    public void desativar(Long id) {
        lojistaProfileUseCase.desativar(id);
    }
}
