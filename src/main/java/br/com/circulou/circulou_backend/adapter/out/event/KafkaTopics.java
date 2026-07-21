package br.com.circulou.circulou_backend.adapter.out.event;

public final class KafkaTopics {

    private KafkaTopics() {
        // Impedir instanciação
    }

    public static final String PEDIDO_CRIADO = "pedido.criado";
    public static final String PEDIDO_CANCELADO = "pedido.cancelado";
    public static final String PEDIDO_LIBERADO = "pedido.liberado";
    public static final String PEDIDO_ENTREGUE = "pedido.entregue";
    public static final String PEDIDO_EM_ROTA = "pedido.em-rota";
    public static final String PEDIDO_PRONTO_PARA_RETIRADA = "pedido.pronto-para-retirada";
    public static final String PEDIDO_EM_PREPARO = "pedido.em-preparo";

}
