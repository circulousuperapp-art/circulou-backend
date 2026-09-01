package br.com.circulou.circulou_backend.model;

import br.com.circulou.circulou_backend.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JornadaTest {

    @Test
    @DisplayName("Deve criar jornada própria válida")
    void deveCriarJornadaPropriaValida() {
        Jornada jornada = new Jornada(1L, JornadaTipo.PROPRIA);
        assertEquals(1L, jornada.getClienteId());
        assertEquals(JornadaTipo.PROPRIA, jornada.getTipo());
        assertEquals(JornadaStatus.CRIADA, jornada.getStatus());
        assertTrue(jornada.getParadas().isEmpty());
    }

    @Test
    @DisplayName("Deve criar jornada motorista válida sem corrida")
    void deveCriarJornadaMotoristaValidaSemCorrida() {
        Jornada jornada = new Jornada(1L, JornadaTipo.MOTORISTA);
        assertNull(jornada.getCorridaId());
        assertEquals(JornadaTipo.MOTORISTA, jornada.getTipo());
    }

    @Test
    @DisplayName("Deve garantir sequência contínua de paradas")
    void deveGarantirSequenciaContinuaDeParadas() {
        Jornada jornada = new Jornada(1L, JornadaTipo.PROPRIA);
        
        // 1. primeira parada deve possuir ordem 1
        jornada.adicionarParada(1L, 1L, 1);
        
        // 2. após ordem 1, próxima deve ser 2
        jornada.adicionarParada(2L, 2L, 2);
        
        assertEquals(2, jornada.getParadas().size());
    }

    @Test
    @DisplayName("Deve rejeitar ordem inicial inválida")
    void deveRejeitarOrdemInicialInvalida() {
        Jornada jornada = new Jornada(1L, JornadaTipo.PROPRIA);
        // 3. rejeitar primeira parada com ordem 2
        assertThrows(BusinessException.class, () -> jornada.adicionarParada(1L, 1L, 2));
    }

    @Test
    @DisplayName("Deve rejeitar saltos na sequência de paradas")
    void deveRejeitarSaltosNaSequencia() {
        Jornada jornada = new Jornada(1L, JornadaTipo.PROPRIA);
        jornada.adicionarParada(1L, 1L, 1);
        
        // 4. rejeitar sequência 1,3
        assertThrows(BusinessException.class, () -> jornada.adicionarParada(2L, 2L, 3));
        
        jornada.adicionarParada(2L, 2L, 2);
        // 5. rejeitar sequência 1,2,4
        assertThrows(BusinessException.class, () -> jornada.adicionarParada(3L, 3L, 4));
    }

    @Test
    @DisplayName("Deve rejeitar ordem duplicada")
    void deveRejeitarOrdemDuplicada() {
        Jornada jornada = new Jornada(1L, JornadaTipo.PROPRIA);
        jornada.adicionarParada(1L, 1L, 1);
        
        // 6. rejeitar ordem duplicada
        assertThrows(BusinessException.class, () -> jornada.adicionarParada(2L, 2L, 1));
    }

    @Test
    @DisplayName("Deve permitir sequência completa e rejeitar sexta parada")
    void devePermitirSequenciaCompletaERejeitarSexta() {
        Jornada jornada = new Jornada(1L, JornadaTipo.PROPRIA);
        // 7. permitir sequência 1,2,3,4,5
        for (int i = 1; i <= 5; i++) {
            jornada.adicionarParada((long) i, (long) i, i);
        }
        assertEquals(5, jornada.getParadas().size());
        
        // 8. rejeitar sexta parada
        assertThrows(BusinessException.class, () -> jornada.adicionarParada(6L, 6L, 6));
    }

    @Test
    @DisplayName("Deve controlar progressão operacional das paradas")
    void deveControlarProgressaoOperacional() {
        Jornada jornada = new Jornada(1L, JornadaTipo.PROPRIA);
        jornada.adicionarParada(1L, 1L, 1);
        jornada.adicionarParada(2L, 2L, 2);
        jornada.iniciar();
        
        // 9. somente primeira parada pode iniciar inicialmente
        jornada.iniciarDeslocamentoProximaParada();
        assertEquals(JornadaParadaStatus.EM_TRANSITO, jornada.getParadas().get(0).getStatus());
        assertEquals(JornadaParadaStatus.AGUARDANDO, jornada.getParadas().get(1).getStatus());

        // 11. parada 2 não pode iniciar enquanto parada 1 estiver EM_TRANSITO
        assertThrows(BusinessException.class, jornada::iniciarDeslocamentoProximaParada);
        
        jornada.registrarChegadaNaLoja();
        // 12. parada 2 não pode iniciar enquanto parada 1 estiver NA_LOJA
        assertThrows(BusinessException.class, jornada::iniciarDeslocamentoProximaParada);
        
        // 13. RETIRADA_REALIZADA da parada 1 libera parada 2
        jornada.confirmarRetirada();
        jornada.iniciarDeslocamentoProximaParada();
        assertEquals(JornadaParadaStatus.EM_TRANSITO, jornada.getParadas().get(1).getStatus());
    }

    @Test
    @DisplayName("Deve permitir próxima parada após cancelamento da anterior")
    void devePermitirProximaAposCancelamento() {
        Jornada jornada = new Jornada(1L, JornadaTipo.PROPRIA);
        jornada.adicionarParada(1L, 1L, 1);
        jornada.adicionarParada(2L, 2L, 2);
        jornada.iniciar();
        
        // 14. CANCELADA da parada 1 libera parada 2
        jornada.cancelarParada(1);
        jornada.iniciarDeslocamentoProximaParada();
        assertEquals(JornadaParadaStatus.EM_TRANSITO, jornada.getParadas().get(1).getStatus());
    }

    @Test
    @DisplayName("Deve permitir próxima parada após múltiplas paradas anteriores terminais")
    void devePermitirProximaAposMultiplasTerminais() {
        Jornada jornada = new Jornada(1L, JornadaTipo.PROPRIA);
        jornada.adicionarParada(1L, 1L, 1);
        jornada.adicionarParada(2L, 2L, 2);
        jornada.adicionarParada(3L, 3L, 3);
        jornada.iniciar();
        
        // 15. múltiplas paradas anteriores terminais liberam corretamente a próxima
        jornada.iniciarDeslocamentoProximaParada();
        jornada.registrarChegadaNaLoja();
        jornada.confirmarRetirada(); // P1 OK
        
        jornada.cancelarParada(2); // P2 CANCELADA
        
        jornada.iniciarDeslocamentoProximaParada(); // P3 deve ser liberada
        assertEquals(JornadaParadaStatus.EM_TRANSITO, jornada.getParadas().get(2).getStatus());
    }

    @Test
    @DisplayName("Não deve permitir duas paradas em execução simultaneamente")
    void naoDevePermitirDuasParadasEmExecucao() {
        Jornada jornada = new Jornada(1L, JornadaTipo.PROPRIA);
        jornada.adicionarParada(1L, 1L, 1);
        jornada.adicionarParada(2L, 2L, 2);
        jornada.iniciar();
        
        jornada.iniciarDeslocamentoProximaParada();
        
        // 16. não permitir duas paradas EM_TRANSITO simultaneamente
        assertThrows(BusinessException.class, jornada::iniciarDeslocamentoProximaParada);
    }

    @Test
    @DisplayName("Deve validar transições terminais da jornada")
    void deveValidarTransicoesTerminais() {
        Jornada jornada = new Jornada(1L, JornadaTipo.PROPRIA);
        jornada.adicionarParada(1L, 1L, 1);
        jornada.iniciar();
        jornada.cancelarParada(1);
        jornada.concluir();
        
        // 18. CONCLUIDA não pode ser cancelada
        assertThrows(BusinessException.class, jornada::cancelar);
        // 19. CONCLUIDA não pode ser reativada
        assertThrows(BusinessException.class, jornada::iniciar);
        
        Jornada jornada2 = new Jornada(1L, JornadaTipo.PROPRIA);
        jornada2.cancelar();
        // 20. CANCELADA não pode ser reativada
        assertThrows(BusinessException.class, jornada2::iniciar);
    }
}
