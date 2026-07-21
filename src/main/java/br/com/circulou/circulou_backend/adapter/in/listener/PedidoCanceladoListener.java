package br.com.circulou.circulou_backend.adapter.in.listener;

import br.com.circulou.circulou_backend.model.event.PedidoCanceladoEvent;
import br.com.circulou.circulou_backend.port.out.PedidoTimerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PedidoCanceladoListener {

    private static final Logger logger = LoggerFactory.getLogger(PedidoCanceladoListener.class);
    private final PedidoTimerPort timerPort;

    public PedidoCanceladoListener(PedidoTimerPort timerPort) {
        this.timerPort = timerPort;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PedidoCanceladoEvent event) {
        logger.info("[DOMAIN EVENT] PedidoCanceladoEvent recebido. Cancelando possíveis timers para PedidoId: {}", event.pedidoId());
        timerPort.cancelarTimer(event.pedidoId());
    }
}
