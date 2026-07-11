package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.UsuarioRequestDTO;
import br.com.circulou.circulou_backend.dto.UsuarioResponseDTO;
import br.com.circulou.circulou_backend.mapper.UsuarioMapper;
import br.com.circulou.circulou_backend.model.Usuario;
import br.com.circulou.circulou_backend.port.in.UsuarioUseCase;
import br.com.circulou.circulou_backend.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsuarioFacadeImpl implements UsuarioUseCase {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioFacadeImpl(UsuarioService usuarioService, 
                             UsuarioMapper usuarioMapper,
                             PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioService.listarTodos()
                .stream()
                .map(usuarioMapper::toResponseDTO)
                .toList();
    }

    @Override
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO salvar(UsuarioRequestDTO dto) {
        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        
        Usuario usuarioSalvo = usuarioService.salvar(usuario);
        return usuarioMapper.toResponseDTO(usuarioSalvo);
    }

    @Override
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioService.buscarPorId(id);
        
        usuarioMapper.updateEntityFromDto(usuario, dto);

        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        Usuario usuarioAtualizado = usuarioService.atualizar(id, usuario);
        return usuarioMapper.toResponseDTO(usuarioAtualizado);
    }

    @Override
    public void deletar(Long id) {
        usuarioService.deletar(id);
    }
}
