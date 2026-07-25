package br.com.circulou.circulou_backend.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CorridaStatusPolicyTest {

    private final CorridaStatusPolicy policy = new CorridaStatusPolicy();

    @ParameterizedTest(name = "De {0} para {1} deve ser válido")
    @MethodSource("transicoesValidas")
    @DisplayName("Deve permitir transições válidas")
    void devePermitirTransicoesValidas(CorridaStatus atual, CorridaStatus novo) {
        assertThat(policy.isTransicaoValida(atual, novo)).isTrue();
    }

    @ParameterizedTest(name = "De {0} para {1} deve ser inválido")
    @MethodSource("transicoesInvalidas")
    @DisplayName("Deve proibir transições inválidas")
    void deveProibirTransicoesInvalidas(CorridaStatus atual, CorridaStatus novo) {
        assertThat(policy.isTransicaoValida(atual, novo)).isFalse();
    }

    @Test
    @DisplayName("Deve permitir transição para o mesmo status")
    void devePermitirMesmoStatus() {
        for (CorridaStatus status : CorridaStatus.values()) {
            assertThat(policy.isTransicaoValida(status, status)).isTrue();
        }
    }

    private static Stream<Arguments> transicoesValidas() {
        return Stream.of(
                Arguments.of(CorridaStatus.SOLICITADA, CorridaStatus.ACEITA),
                Arguments.of(CorridaStatus.SOLICITADA, CorridaStatus.CANCELADA),
                Arguments.of(CorridaStatus.ACEITA, CorridaStatus.MOTORISTA_A_CAMINHO),
                Arguments.of(CorridaStatus.ACEITA, CorridaStatus.CANCELADA),
                Arguments.of(CorridaStatus.MOTORISTA_A_CAMINHO, CorridaStatus.CLIENTE_EMBARCADO),
                Arguments.of(CorridaStatus.MOTORISTA_A_CAMINHO, CorridaStatus.CANCELADA),
                Arguments.of(CorridaStatus.CLIENTE_EMBARCADO, CorridaStatus.PARADA_NA_LOJA),
                Arguments.of(CorridaStatus.CLIENTE_EMBARCADO, CorridaStatus.EM_DESLOCAMENTO),
                Arguments.of(CorridaStatus.CLIENTE_EMBARCADO, CorridaStatus.CANCELADA),
                Arguments.of(CorridaStatus.PARADA_NA_LOJA, CorridaStatus.EM_DESLOCAMENTO),
                Arguments.of(CorridaStatus.PARADA_NA_LOJA, CorridaStatus.CANCELADA),
                Arguments.of(CorridaStatus.EM_DESLOCAMENTO, CorridaStatus.FINALIZADA),
                Arguments.of(CorridaStatus.EM_DESLOCAMENTO, CorridaStatus.CANCELADA)
        );
    }

    private static Stream<Arguments> transicoesInvalidas() {
        return Stream.of(
                Arguments.of(CorridaStatus.SOLICITADA, CorridaStatus.FINALIZADA),
                Arguments.of(CorridaStatus.FINALIZADA, CorridaStatus.ACEITA),
                Arguments.of(CorridaStatus.CANCELADA, CorridaStatus.SOLICITADA),
                Arguments.of(CorridaStatus.EM_DESLOCAMENTO, CorridaStatus.SOLICITADA),
                Arguments.of(CorridaStatus.PARADA_NA_LOJA, CorridaStatus.ACEITA)
        );
    }
}
