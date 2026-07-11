package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.UsuarioRequestDTO;
import br.com.circulou.circulou_backend.dto.UsuarioResponseDTO;
import br.com.circulou.circulou_backend.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getTelefone(),
                usuario.getFotoPerfil(),
                usuario.getAtivo()
        );
    }

    public Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        updateEntityFromDto(usuario, dto);
        usuario.setSenha(dto.getSenha());
        return usuario;
    }

    public void updateEntityFromDto(Usuario usuario, UsuarioRequestDTO dto) {
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setRole(dto.getRole());
        usuario.setTelefone(dto.getTelefone());
        usuario.setFotoPerfil(dto.getFotoPerfil());
        usuario.setAtivo(dto.getAtivo());
    }
}
