package br.com.circulou.circulou_backend.port.out;

import java.time.LocalDateTime;

public interface PedidoTimerPort {
    void agendarLiberacaoAutomatica(Long pedidoId, LocalDateTime dataExecucao);
    void cancelarTimer(Long pedidoId);
}
