package br.com.circulou.circulou_backend.adapter.in.listener;

import br.com.circulou.circulou_backend.model.event.PedidoLiberadoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PedidoLiberadoListener {

    private static final Logger logger = LoggerFactory.getLogger(PedidoLiberadoListener.class);

    @Async
    @EventListener
    public void handle(PedidoLiberadoEvent event) {
        logger.info("[DOMAIN EVENT] PedidoLiberadoEvent recebido. PedidoId: {}. Notificando estabelecimento para iniciar preparo.", event.pedidoId());
    }
}
