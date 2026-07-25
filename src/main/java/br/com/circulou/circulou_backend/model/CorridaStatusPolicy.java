package br.com.circulou.circulou_backend.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class CorridaStatusPolicy {

    private static final Map<CorridaStatus, Set<CorridaStatus>> TRANSICOES_PERMITIDAS = Map.of(
            CorridaStatus.SOLICITADA, EnumSet.of(CorridaStatus.ACEITA, CorridaStatus.CANCELADA),
            CorridaStatus.ACEITA, EnumSet.of(CorridaStatus.MOTORISTA_A_CAMINHO, CorridaStatus.CANCELADA),
            CorridaStatus.MOTORISTA_A_CAMINHO, EnumSet.of(CorridaStatus.CLIENTE_EMBARCADO, CorridaStatus.CANCELADA),
            CorridaStatus.CLIENTE_EMBARCADO, EnumSet.of(CorridaStatus.PARADA_NA_LOJA, CorridaStatus.EM_DESLOCAMENTO, CorridaStatus.CANCELADA),
            CorridaStatus.PARADA_NA_LOJA, EnumSet.of(CorridaStatus.EM_DESLOCAMENTO, CorridaStatus.CANCELADA),
            CorridaStatus.EM_DESLOCAMENTO, EnumSet.of(CorridaStatus.FINALIZADA, CorridaStatus.CANCELADA),
            CorridaStatus.FINALIZADA, EnumSet.noneOf(CorridaStatus.class),
            CorridaStatus.CANCELADA, EnumSet.noneOf(CorridaStatus.class)
    );

    public boolean isTransicaoValida(CorridaStatus atual, CorridaStatus novo) {
        if (atual == novo) return true;
        Set<CorridaStatus> permitidos = TRANSICOES_PERMITIDAS.getOrDefault(atual, EnumSet.noneOf(CorridaStatus.class));
        return permitidos.contains(novo);
    }
}
