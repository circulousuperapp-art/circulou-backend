package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.model.event.*;
import br.com.circulou.circulou_backend.port.out.TopicRegistryPort;
import org.springframework.stereotype.Component;

@Component
public class DefaultTopicRegistryAdapter implements TopicRegistryPort {

    @Override
    public String resolveTopic(DomainEvent event) {
        if (event instanceof PedidoCriadoEvent) return KafkaTopics.PEDIDO_CRIADO;
        if (event instanceof PedidoCanceladoEvent) return KafkaTopics.PEDIDO_CANCELADO;
        if (event instanceof PedidoLiberadoEvent) return KafkaTopics.PEDIDO_LIBERADO;
        if (event instanceof PedidoEntregueEvent) return KafkaTopics.PEDIDO_ENTREGUE;
        if (event instanceof PedidoEmRotaEvent) return KafkaTopics.PEDIDO_EM_ROTA;
        if (event instanceof PedidoProntoParaRetiradaEvent) return KafkaTopics.PEDIDO_PRONTO_PARA_RETIRADA;
        if (event instanceof PedidoEmPreparoEvent) return KafkaTopics.PEDIDO_EM_PREPARO;
        
        return "circulou.events." + event.getClass().getSimpleName()
                .replaceAll("([a-z])([A-Z])", "$1-$2")
                .toLowerCase();
    }
}
