package br.com.circulou.circulou_backend.model;

import br.com.circulou.circulou_backend.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JornadaParadaTest {

    @Test
    @DisplayName("Deve progressar status da parada normalmente")
    void deveProgressarStatusDaParadaNormalmente() {
        Jornada jornada = new Jornada(1L, JornadaTipo.PROPRIA);
        JornadaParada parada = new JornadaParada(jornada, 1L, 1L, 1);
        
        assertEquals(JornadaParadaStatus.AGUARDANDO, parada.getStatus());
        
        parada.iniciarDeslocamento();
        assertEquals(JornadaParadaStatus.EM_TRANSITO, parada.getStatus());
        
        parada.registrarChegadaNaLoja();
        assertEquals(JornadaParadaStatus.NA_LOJA, parada.getStatus());
        
        parada.confirmarRetirada();
        assertEquals(JornadaParadaStatus.RETIRADA_REALIZADA, parada.getStatus());
        assertTrue(parada.isTerminal());
    }

    @Test
    @DisplayName("Deve impedir transição fora de ordem")
    void deveImpedirTransicaoForaDeOrdem() {
        Jornada jornada = new Jornada(1L, JornadaTipo.PROPRIA);
        JornadaParada parada = new JornadaParada(jornada, 1L, 1L, 1);
        
        assertThrows(BusinessException.class, parada::registrarChegadaNaLoja);
        assertThrows(BusinessException.class, parada::confirmarRetirada);
    }

    @Test
    @DisplayName("Deve impedir transição após estado terminal")
    void deveImpedirTransicaoAposEstadoTerminal() {
        Jornada jornada = new Jornada(1L, JornadaTipo.PROPRIA);
        JornadaParada parada = new JornadaParada(jornada, 1L, 1L, 1);
        
        parada.cancelar();
        assertThrows(BusinessException.class, parada::iniciarDeslocamento);
        assertThrows(BusinessException.class, parada::confirmarRetirada);
    }

    @Test
    @DisplayName("Deve validar ordem da parada no construtor")
    void deveValidarOrdemNoConstrutor() {
        Jornada jornada = new Jornada(1L, JornadaTipo.PROPRIA);
        assertThrows(BusinessException.class, () -> new JornadaParada(jornada, 1L, 1L, 0));
        assertThrows(BusinessException.class, () -> new JornadaParada(jornada, 1L, 1L, 6));
    }
}
