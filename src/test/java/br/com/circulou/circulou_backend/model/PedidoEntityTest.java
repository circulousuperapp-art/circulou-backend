package br.com.circulou.circulou_backend.model;

import br.com.circulou.circulou_backend.exception.BusinessException;
import br.com.circulou.circulou_backend.model.event.PedidoCriadoEvent;
import br.com.circulou.circulou_backend.model.event.PedidoLiberadoEvent;
import br.com.circulou.circulou_backend.model.event.PedidoEmPreparoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoEntityTest {

    private Pedido pedido;
    private PedidoStatusPolicy policy;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setId(1L);
        policy = new PedidoStatusPolicy();
    }

    @Test
    @DisplayName("Deve transitar status e registrar eventos corretamente")
    void deveTransitarERegistrarEventos() {
        // Transição inicial PENDENTE -> AGUARDANDO_LIBERACAO
        pedido.transitarPara(PedidoStatus.AGUARDANDO_LIBERACAO, policy);
        
        assertEquals(PedidoStatus.AGUARDANDO_LIBERACAO, pedido.getStatus());
        List<Object> events = pedido.pullDomainEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof PedidoCriadoEvent);

        // Próxima transição AGUARDANDO_LIBERACAO -> EM_PREPARO
        pedido.transitarPara(PedidoStatus.EM_PREPARO, policy);
        assertEquals(PedidoStatus.EM_PREPARO, pedido.getStatus());
        
        events = pedido.pullDomainEvents();
        assertEquals(2, events.size()); // PedidoLiberadoEvent e PedidoEmPreparoEvent
        assertTrue(events.stream().anyMatch(e -> e instanceof PedidoLiberadoEvent));
        assertTrue(events.stream().anyMatch(e -> e instanceof PedidoEmPreparoEvent));
    }

    @Test
    @DisplayName("Deve lançar exceção para transição inválida")
    void deveLancarExcecaoTransicaoInvalida() {
        assertThrows(BusinessException.class, () -> 
            pedido.transitarPara(PedidoStatus.ENTREGUE, policy)
        );
    }

    @Test
    @DisplayName("Deve limpar eventos após pull")
    void deveLimparEventosAposPull() {
        pedido.transitarPara(PedidoStatus.AGUARDANDO_LIBERACAO, policy);
        assertFalse(pedido.pullDomainEvents().isEmpty());
        assertTrue(pedido.pullDomainEvents().isEmpty());
    }
}
