package br.com.circulou.circulou_backend.adapter.out.timer;

import br.com.circulou.circulou_backend.model.PedidoStatus;
import br.com.circulou.circulou_backend.port.out.PedidoTimerPort;
import br.com.circulou.circulou_backend.service.PedidoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
public class PedidoTimerAdapter implements PedidoTimerPort {

    private static final Logger logger = LoggerFactory.getLogger(PedidoTimerAdapter.class);
    private final TaskScheduler taskScheduler;
    private final PedidoService pedidoService;
    private final Map<Long, ScheduledFuture<?>> agendamentos = new ConcurrentHashMap<>();

    public PedidoTimerAdapter(TaskScheduler taskScheduler, @Lazy PedidoService pedidoService) {
        this.taskScheduler = taskScheduler;
        this.pedidoService = pedidoService;
    }

    @Override
    public void agendarLiberacaoAutomatica(Long pedidoId, LocalDateTime dataExecucao) {
        logger.info("[TIMER] Agendando liberação automática do pedido {} para {}", pedidoId, dataExecucao);
        
        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            try {
                logger.info("[TIMER] Executando liberação automática do pedido {}", pedidoId);
                pedidoService.alterarStatus(pedidoId, PedidoStatus.EM_PREPARO);
                agendamentos.remove(pedidoId);
            } catch (Exception e) {
                logger.error("[TIMER] Erro ao executar liberação automática do pedido {}", pedidoId, e);
            }
        }, dataExecucao.atZone(ZoneId.systemDefault()).toInstant());

        agendamentos.put(pedidoId, future);
    }

    @Override
    public void cancelarTimer(Long pedidoId) {
        ScheduledFuture<?> future = agendamentos.remove(pedidoId);
        if (future != null && !future.isDone()) {
            logger.info("[TIMER] Cancelando timer do pedido {}", pedidoId);
            future.cancel(false);
        }
    }
}
