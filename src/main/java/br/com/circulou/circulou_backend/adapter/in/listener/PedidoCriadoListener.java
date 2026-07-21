package br.com.circulou.circulou_backend.adapter.in.listener;

import br.com.circulou.circulou_backend.model.Pedido;
import br.com.circulou.circulou_backend.model.event.PedidoCriadoEvent;
import br.com.circulou.circulou_backend.port.out.PedidoTimerPort;
import br.com.circulou.circulou_backend.service.PedidoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PedidoCriadoListener {

    private static final Logger logger = LoggerFactory.getLogger(PedidoCriadoListener.class);
    private final PedidoTimerPort timerPort;
    private final PedidoService pedidoService;

    public PedidoCriadoListener(PedidoTimerPort timerPort, PedidoService pedidoService) {
        this.timerPort = timerPort;
        this.pedidoService = pedidoService;
    }

    @Async
    @EventListener
    public void handle(PedidoCriadoEvent event) {
        logger.info("[DOMAIN EVENT] PedidoCriadoEvent recebido. PedidoId: {}", event.pedidoId());

        try {
            Pedido pedido = pedidoService.buscarPorId(event.pedidoId());
            if (pedido.getDataLimiteCancelamento() != null) {
                logger.info("[DOMAIN EVENT] Iniciando timer de cancelamento para o pedido {}. Limite: {}", 
                        pedido.getId(), pedido.getDataLimiteCancelamento());
                timerPort.agendarLiberacaoAutomatica(pedido.getId(), pedido.getDataLimiteCancelamento());
            } else {
                logger.warn("[DOMAIN EVENT] Pedido {} sem data limite de cancelamento. Timer não agendado.", pedido.getId());
            }
        } catch (Exception e) {
            logger.error("[DOMAIN EVENT] Erro ao processar PedidoCriadoEvent para o pedido {}", event.pedidoId(), e);
        }
    }
}
