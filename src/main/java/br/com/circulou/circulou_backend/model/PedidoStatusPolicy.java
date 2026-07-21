package br.com.circulou.circulou_backend.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class PedidoStatusPolicy {

    private static final Map<PedidoStatus, Set<PedidoStatus>> TRANSICOES_PERMITIDAS = Map.of(
            PedidoStatus.PENDENTE, EnumSet.of(PedidoStatus.AGUARDANDO_LIBERACAO, PedidoStatus.CANCELADO),
            PedidoStatus.AGUARDANDO_LIBERACAO, EnumSet.of(PedidoStatus.EM_PREPARO, PedidoStatus.CANCELADO),
            PedidoStatus.EM_PREPARO, EnumSet.of(PedidoStatus.PRONTO_PARA_RETIRADA, PedidoStatus.CANCELADO),
            PedidoStatus.PRONTO_PARA_RETIRADA, EnumSet.of(PedidoStatus.EM_ROTA, PedidoStatus.CANCELADO),
            PedidoStatus.EM_ROTA, EnumSet.of(PedidoStatus.ENTREGUE, PedidoStatus.CANCELADO),
            PedidoStatus.ENTREGUE, EnumSet.noneOf(PedidoStatus.class),
            PedidoStatus.CANCELADO, EnumSet.noneOf(PedidoStatus.class)
    );

    public boolean isTransicaoValida(PedidoStatus atual, PedidoStatus novo) {
        if (atual == novo) return true;
        Set<PedidoStatus> permitidos = TRANSICOES_PERMITIDAS.getOrDefault(atual, EnumSet.noneOf(PedidoStatus.class));
        return permitidos.contains(novo);
    }
}
