package br.com.circulou.circulou_backend.adapter.out.event;

import br.com.circulou.circulou_backend.model.event.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class EventCatalog {

    private final Map<Class<?>, EventInfo> catalog;

    public EventCatalog() {
        Map<Class<?>, EventInfo> map = new HashMap<>();
        
        map.put(PedidoCriadoEvent.class, new EventInfo(
                KafkaTopics.PEDIDO_CRIADO, 
                1, 
                "circulou-backend", 
                "Evento disparado quando um novo pedido é criado no sistema."));
        
        map.put(PedidoCanceladoEvent.class, new EventInfo(
                KafkaTopics.PEDIDO_CANCELADO, 
                1, 
                "circulou-backend", 
                "Evento disparado quando um pedido é cancelado."));
        
        map.put(PedidoLiberadoEvent.class, new EventInfo(
                KafkaTopics.PEDIDO_LIBERADO, 
                1, 
                "circulou-backend", 
                "Evento disparado quando o pagamento do pedido é confirmado e ele está liberado para preparo."));
        
        map.put(PedidoEmPreparoEvent.class, new EventInfo(
                KafkaTopics.PEDIDO_EM_PREPARO, 
                1, 
                "circulou-backend", 
                "Evento disparado quando a loja inicia o preparo do pedido."));
        
        map.put(PedidoProntoParaRetiradaEvent.class, new EventInfo(
                KafkaTopics.PEDIDO_PRONTO_PARA_RETIRADA, 
                1, 
                "circulou-backend", 
                "Evento disparado quando o pedido está pronto para ser coletado pelo motorista."));
        
        map.put(PedidoEmRotaEvent.class, new EventInfo(
                KafkaTopics.PEDIDO_EM_ROTA, 
                1, 
                "circulou-backend", 
                "Evento disparado quando o motorista inicia a entrega do pedido."));
        
        map.put(PedidoEntregueEvent.class, new EventInfo(
                KafkaTopics.PEDIDO_ENTREGUE, 
                1, 
                "circulou-backend", 
                "Evento disparado quando o pedido é entregue ao cliente final."));

        this.catalog = Collections.unmodifiableMap(map);
    }

    public Optional<EventInfo> getInfo(Class<?> eventClass) {
        return Optional.ofNullable(catalog.get(eventClass));
    }

    public record EventInfo(
            String topic,
            int version,
            String producer,
            String description
    ) {}
}
