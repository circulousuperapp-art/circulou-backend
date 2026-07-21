package br.com.circulou.circulou_backend.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoStatusPolicyTest {

    private final PedidoStatusPolicy policy = new PedidoStatusPolicy();

    @ParameterizedTest
    @CsvSource({
            "PENDENTE, AGUARDANDO_LIBERACAO, true",
            "PENDENTE, CANCELADO, true",
            "PENDENTE, EM_PREPARO, false",
            "AGUARDANDO_LIBERACAO, EM_PREPARO, true",
            "AGUARDANDO_LIBERACAO, CANCELADO, true",
            "AGUARDANDO_LIBERACAO, ENTREGUE, false",
            "EM_PREPARO, PRONTO_PARA_RETIRADA, true",
            "EM_PREPARO, CANCELADO, true",
            "EM_PREPARO, ENTREGUE, false",
            "PRONTO_PARA_RETIRADA, EM_ROTA, true",
            "EM_ROTA, ENTREGUE, true",
            "ENTREGUE, CANCELADO, false",
            "CANCELADO, PENDENTE, false"
    })
    @DisplayName("Deve validar transições de status corretamente")
    void deveValidarTransicoes(PedidoStatus atual, PedidoStatus novo, boolean esperado) {
        assertEquals(esperado, policy.isTransicaoValida(atual, novo));
    }

    @Test
    @DisplayName("Deve permitir transição para o mesmo status")
    void devePermitirMesmoStatus() {
        assertEquals(true, policy.isTransicaoValida(PedidoStatus.EM_PREPARO, PedidoStatus.EM_PREPARO));
    }
}
