package br.com.circulou.circulou_backend.adapter.in.listener;

import br.com.circulou.circulou_backend.model.event.PedidoLiberadoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PedidoLiberadoListener {

    private static final Logger logger = LoggerFactory.getLogger(PedidoLiberadoListener.class);

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PedidoLiberadoEvent event) {
        logger.info("[DOMAIN EVENT] PedidoLiberadoEvent recebido. PedidoId: {}. Notificando estabelecimento para iniciar preparo.", event.pedidoId());
    }
}
