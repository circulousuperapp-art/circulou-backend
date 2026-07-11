package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.UsuarioRequestDTO;
import br.com.circulou.circulou_backend.dto.UsuarioResponseDTO;
import br.com.circulou.circulou_backend.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    private UsuarioMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UsuarioMapper();
    }

    @Test
    void toResponseDTO_ShouldMapCorrectly() {
        // Given
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setEmail("teste@usuario.com");
        usuario.setRole("USER");
        usuario.setTelefone("11999999999");
        usuario.setFotoPerfil("http://foto.com/1");
        usuario.setAtivo(true);

        // When
        UsuarioResponseDTO responseDTO = mapper.toResponseDTO(usuario);

        // Then
        assertNotNull(responseDTO);
        assertEquals(usuario.getId(), responseDTO.getId());
        assertEquals(usuario.getNome(), responseDTO.getNome());
        assertEquals(usuario.getEmail(), responseDTO.getEmail());
        assertEquals(usuario.getRole(), responseDTO.getRole());
        assertEquals(usuario.getTelefone(), responseDTO.getTelefone());
        assertEquals(usuario.getFotoPerfil(), responseDTO.getFotoPerfil());
        assertEquals(usuario.getAtivo(), responseDTO.getAtivo());
    }

    @Test
    void toEntity_ShouldMapCorrectly() {
        // Given
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setNome("Novo Usuário");
        requestDTO.setEmail("novo@usuario.com");
        requestDTO.setSenha("senha123");
        requestDTO.setRole("ADMIN");
        requestDTO.setTelefone("11888888888");
        requestDTO.setFotoPerfil("http://foto.com/2");
        requestDTO.setAtivo(false);

        // When
        Usuario usuario = mapper.toEntity(requestDTO);

        // Then
        assertNotNull(usuario);
        assertEquals(requestDTO.getNome(), usuario.getNome());
        assertEquals(requestDTO.getEmail(), usuario.getEmail());
        assertEquals(requestDTO.getSenha(), usuario.getSenha());
        assertEquals(requestDTO.getRole(), usuario.getRole());
        assertEquals(requestDTO.getTelefone(), usuario.getTelefone());
        assertEquals(requestDTO.getFotoPerfil(), usuario.getFotoPerfil());
        assertEquals(requestDTO.getAtivo(), usuario.getAtivo());
    }

    @Test
    void updateEntityFromDto_ShouldUpdateFields() {
        // Given
        Usuario usuario = new Usuario();
        usuario.setNome("Antigo Nome");

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setNome("Novo Nome");
        requestDTO.setEmail("novo@email.com");

        // When
        mapper.updateEntityFromDto(usuario, requestDTO);

        // Then
        assertEquals(requestDTO.getNome(), usuario.getNome());
        assertEquals(requestDTO.getEmail(), usuario.getEmail());
    }
}
