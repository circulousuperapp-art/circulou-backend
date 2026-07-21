package br.com.circulou.circulou_backend.adapter.in.listener;

import br.com.circulou.circulou_backend.model.event.PedidoEntregueEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PedidoEntregueListener {

    private static final Logger logger = LoggerFactory.getLogger(PedidoEntregueListener.class);

    @Async
    @EventListener
    public void handle(PedidoEntregueEvent event) {
        logger.info("[DOMAIN EVENT] PedidoEntregueEvent recebido. PedidoId: {}. Finalizando fluxo comercial e financeiro.", event.pedidoId());
    }
}
