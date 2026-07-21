package br.com.circulou.circulou_backend.adapter.in.listener;

import br.com.circulou.circulou_backend.model.event.PedidoCanceladoEvent;
import br.com.circulou.circulou_backend.port.out.PedidoTimerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PedidoCanceladoListener {

    private static final Logger logger = LoggerFactory.getLogger(PedidoCanceladoListener.class);
    private final PedidoTimerPort timerPort;

    public PedidoCanceladoListener(PedidoTimerPort timerPort) {
        this.timerPort = timerPort;
    }

    @Async
    @EventListener
    public void handle(PedidoCanceladoEvent event) {
        logger.info("[DOMAIN EVENT] PedidoCanceladoEvent recebido. Cancelando possíveis timers para PedidoId: {}", event.pedidoId());
        timerPort.cancelarTimer(event.pedidoId());
    }
}
