package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.LojaRequestDTO;
import br.com.circulou.circulou_backend.dto.LojaResponseDTO;
import br.com.circulou.circulou_backend.model.Endereco;
import br.com.circulou.circulou_backend.model.Loja;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LojaMapperTest {

    private LojaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LojaMapper();
    }

    @Test
    void toResponseDTO_ShouldMapCorrectly() {
        // Given
        Loja loja = new Loja();
        loja.setId(1L);
        loja.setNome("Loja Teste");
        loja.setEmail("teste@loja.com");
        loja.setTelefone("1199999999");
        loja.setLogo("http://logo.com/1");
        loja.setTempoMedioPreparo(30);
        loja.setAtiva(true);
        loja.setSeloConfianca(true);

        // When
        LojaResponseDTO responseDTO = mapper.toResponseDTO(loja);

        // Then
        assertNotNull(responseDTO);
        assertEquals(loja.getId(), responseDTO.getId());
        assertEquals(loja.getNome(), responseDTO.getNome());
        assertEquals(loja.getEmail(), responseDTO.getEmail());
        assertEquals(loja.getTelefone(), responseDTO.getTelefone());
        assertEquals(loja.getLogo(), responseDTO.getLogo());
        assertEquals(loja.getTempoMedioPreparo(), responseDTO.getTempoMedioPreparo());
        assertEquals(loja.getAtiva(), responseDTO.getAtiva());
        assertEquals(loja.getSeloConfianca(), responseDTO.getSeloConfianca());
    }

    @Test
    void toEntity_ShouldMapCorrectly() {
        // Given
        Endereco endereco = new Endereco();
        endereco.setCidade("São Paulo");

        LojaRequestDTO requestDTO = new LojaRequestDTO();
        requestDTO.setNome("Nova Loja");
        requestDTO.setEmail("nova@loja.com");
        requestDTO.setSenha("senha123");
        requestDTO.setTelefone("1188888888");
        requestDTO.setLogo("http://logo.com/2");
        requestDTO.setTempoMedioPreparo(45);
        requestDTO.setAtiva(false);
        requestDTO.setSeloConfianca(false);
        requestDTO.setEndereco(endereco);

        // When
        Loja loja = mapper.toEntity(requestDTO);

        // Then
        assertNotNull(loja);
        assertEquals(requestDTO.getNome(), loja.getNome());
        assertEquals(requestDTO.getEmail(), loja.getEmail());
        assertEquals(requestDTO.getSenha(), loja.getSenha());
        assertEquals(requestDTO.getTelefone(), loja.getTelefone());
        assertEquals(requestDTO.getLogo(), loja.getLogo());
        assertEquals(requestDTO.getTempoMedioPreparo(), loja.getTempoMedioPreparo());
        assertEquals(requestDTO.getAtiva(), loja.getAtiva());
        assertEquals(requestDTO.getSeloConfianca(), loja.getSeloConfianca());
        assertEquals(requestDTO.getEndereco(), loja.getEndereco());
    }

    @Test
    void updateEntityFromDto_ShouldUpdateFields() {
        // Given
        Loja loja = new Loja();
        loja.setNome("Antigo Nome");

        Endereco novoEndereco = new Endereco();
        novoEndereco.setCidade("Rio de Janeiro");

        LojaRequestDTO requestDTO = new LojaRequestDTO();
        requestDTO.setNome("Novo Nome");
        requestDTO.setEmail("novo@email.com");
        requestDTO.setEndereco(novoEndereco);

        // When
        mapper.updateEntityFromDto(loja, requestDTO);

        // Then
        assertEquals(requestDTO.getNome(), loja.getNome());
        assertEquals(requestDTO.getEmail(), loja.getEmail());
        assertEquals(requestDTO.getEndereco(), loja.getEndereco());
    }
}
