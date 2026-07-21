package br.com.circulou.circulou_backend.adapter.in.listener;

import br.com.circulou.circulou_backend.model.event.PedidoEntregueEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PedidoEntregueListener {

    private static final Logger logger = LoggerFactory.getLogger(PedidoEntregueListener.class);

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PedidoEntregueEvent event) {
        logger.info("[DOMAIN EVENT] PedidoEntregueEvent recebido. PedidoId: {}. Finalizando fluxo comercial e financeiro.", event.pedidoId());
    }
}
