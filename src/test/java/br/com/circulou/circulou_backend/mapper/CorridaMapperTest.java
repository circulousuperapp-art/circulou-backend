package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.CorridaResponseDTO;
import br.com.circulou.circulou_backend.model.Corrida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CorridaMapperTest {

    private CorridaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CorridaMapper();
    }

    @Test
    void deveFormatarDistanciaEDuracaoCorretamente() {
        Corrida corrida = new Corrida();
        corrida.setDistanciaEstimadaMetros(4226);
        corrida.setDuracaoEstimadaSegundos(774);

        CorridaResponseDTO response = mapper.toResponseDTO(corrida);

        assertEquals(4226, response.getDistanciaMetros());
        assertEquals(774, response.getDuracaoSegundos());
        assertEquals("4,2 km", response.getDistanciaFormatada());
        assertEquals("13 min", response.getDuracaoFormatada());
    }

    @Test
    void deveRetornarNullQuandoValoresForemNulos() {
        Corrida corrida = new Corrida();
        corrida.setDistanciaEstimadaMetros(null);
        corrida.setDuracaoEstimadaSegundos(null);

        CorridaResponseDTO response = mapper.toResponseDTO(corrida);

        assertNull(response.getDistanciaFormatada());
        assertNull(response.getDuracaoFormatada());
    }

    @Test
    void deveFormatarValoresZero() {
        Corrida corrida = new Corrida();
        corrida.setDistanciaEstimadaMetros(0);
        corrida.setDuracaoEstimadaSegundos(0);

        CorridaResponseDTO response = mapper.toResponseDTO(corrida);

        assertEquals("0,0 km", response.getDistanciaFormatada());
        assertEquals("0 min", response.getDuracaoFormatada());
    }

    @Test
    void deveArredondarMinutosCorretamente() {
        Corrida corrida = new Corrida();

        // 89 segundos / 60 = 1.48 -> 1 min
        corrida.setDuracaoEstimadaSegundos(89);
        assertEquals("1 min", mapper.toResponseDTO(corrida).getDuracaoFormatada());

        // 91 segundos / 60 = 1.51 -> 2 min
        corrida.setDuracaoEstimadaSegundos(91);
        assertEquals("2 min", mapper.toResponseDTO(corrida).getDuracaoFormatada());
    }
}
