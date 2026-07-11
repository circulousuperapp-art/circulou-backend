package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.LojaRequestDTO;
import br.com.circulou.circulou_backend.dto.LojaResponseDTO;
import br.com.circulou.circulou_backend.mapper.LojaMapper;
import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.port.in.LojaUseCase;
import br.com.circulou.circulou_backend.service.LojaService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LojaFacadeImpl implements LojaUseCase {

    private final LojaService lojaService;
    private final LojaMapper lojaMapper;
    private final PasswordEncoder passwordEncoder;

    public LojaFacadeImpl(LojaService lojaService, 
                          LojaMapper lojaMapper,
                          PasswordEncoder passwordEncoder) {
        this.lojaService = lojaService;
        this.lojaMapper = lojaMapper;
        this.passwordEncoder = passwordEncoder;
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
        loja.setSenha(passwordEncoder.encode(loja.getSenha()));
        
        Loja lojaSalva = lojaService.salvar(loja);
        return lojaMapper.toResponseDTO(lojaSalva);
    }

    @Override
    public LojaResponseDTO atualizar(Long id, LojaRequestDTO dto) {
        Loja loja = lojaService.buscarPorId(id);

        lojaMapper.updateEntityFromDto(loja, dto);
        
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            loja.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        Loja lojaAtualizada = lojaService.atualizar(id, loja);
        return lojaMapper.toResponseDTO(lojaAtualizada);
    }

    @Override
    public void deletar(Long id) {
        lojaService.deletar(id);
    }
}
