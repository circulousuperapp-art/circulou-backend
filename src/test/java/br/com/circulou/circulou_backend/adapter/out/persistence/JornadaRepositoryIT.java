package br.com.circulou.circulou_backend.adapter.out.persistence;

import br.com.circulou.circulou_backend.integration.BaseIntegrationTest;
import br.com.circulou.circulou_backend.model.Jornada;
import br.com.circulou.circulou_backend.model.JornadaParada;
import br.com.circulou.circulou_backend.model.JornadaParadaStatus;
import br.com.circulou.circulou_backend.model.JornadaStatus;
import br.com.circulou.circulou_backend.model.JornadaTipo;
import br.com.circulou.circulou_backend.port.out.JornadaRepositoryPort;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class JornadaRepositoryIT extends BaseIntegrationTest {

    @Autowired
    private JornadaRepositoryPort jornadaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Deve persistir e recuperar uma jornada com paradas preservando a ordem")
    void devePersistirERecuperarJornadaComParadas() {
        // 1. Criação de Jornada válida
        Jornada jornada = new Jornada(100L, JornadaTipo.MOTORISTA);
        jornada.setCorridaId(200L);

        // 2. Inclusão de mais de uma JornadaParada
        jornada.adicionarParada(1L, 10L, 1);
        jornada.adicionarParada(2L, 20L, 2);

        // 3. Persistência através da camada apropriada
        Jornada salva = jornadaRepository.save(jornada);
        assertNotNull(salva.getId());

        // Forçar round-trip real de persistência
        entityManager.flush();
        entityManager.clear();

        // 4. Recuperação da Jornada persistida
        Optional<Jornada> recuperadaOpt = jornadaRepository.findById(salva.getId());
        assertTrue(recuperadaOpt.isPresent());
        Jornada recuperada = recuperadaOpt.get();

        // 7. Persistência correta dos principais campos
        assertEquals(100L, recuperada.getClienteId());
        assertEquals(200L, recuperada.getCorridaId());
        assertEquals(JornadaTipo.MOTORISTA, recuperada.getTipo());
        assertEquals(JornadaStatus.CRIADA, recuperada.getStatus());

        // 5. Recuperação das paradas pertencentes ao aggregate
        List<JornadaParada> paradas = recuperada.getParadas();
        assertEquals(2, paradas.size());

        // 6. Preservação da ordem das paradas (ordenado por @OrderBy("ordem ASC"))
        assertEquals(1, paradas.get(0).getOrdem());
        assertEquals(2, paradas.get(1).getOrdem());

        // Verificação dos campos das paradas
        assertEquals(1L, paradas.get(0).getLojaId());
        assertEquals(10L, paradas.get(0).getPedidoId());
        assertEquals(JornadaParadaStatus.AGUARDANDO, paradas.get(0).getStatus());
        assertEquals(2L, paradas.get(1).getLojaId());
        assertEquals(20L, paradas.get(1).getPedidoId());
        assertEquals(JornadaParadaStatus.AGUARDANDO, paradas.get(1).getStatus());
    }

    @Test
    @DisplayName("Deve persistir jornada própria sem corrida")
    void devePersistirJornadaPropriaSemCorrida() {
        Jornada jornada = new Jornada(101L, JornadaTipo.PROPRIA);
        jornada.adicionarParada(3L, 30L, 1);

        Jornada salva = jornadaRepository.save(jornada);

        entityManager.flush();
        entityManager.clear();

        Jornada recuperada = jornadaRepository.findById(salva.getId()).orElseThrow();
        assertEquals(JornadaTipo.PROPRIA, recuperada.getTipo());
        assertNull(recuperada.getCorridaId());
    }
}
