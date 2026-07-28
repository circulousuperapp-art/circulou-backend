package br.com.circulou.circulou_backend.adapter.out.observability;

import br.com.circulou.circulou_backend.port.out.MetricsPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MicrometerMetricsAdapter implements MetricsPort {

    private final Counter usuariosCounter;
    private final Counter lojasCounter;
    private final Counter produtosCounter;
    private final Counter pedidosCounter;
    private final Counter corridasCounter;

    public MicrometerMetricsAdapter(MeterRegistry registry) {
        this.usuariosCounter = Counter.builder("circulou.usuarios.cadastrados")
                .description("Total de usuários cadastrados no sistema")
                .register(registry);

        this.lojasCounter = Counter.builder("circulou.lojas.cadastradas")
                .description("Total de lojas cadastradas no sistema")
                .register(registry);

        this.produtosCounter = Counter.builder("circulou.produtos.cadastrados")
                .description("Total de produtos cadastrados no sistema")
                .register(registry);

        this.pedidosCounter = Counter.builder("circulou.pedidos.criados")
                .description("Total de pedidos criados no sistema")
                .register(registry);

        this.corridasCounter = Counter.builder("circulou.corridas.criadas")
                .description("Total de corridas solicitadas no sistema")
                .register(registry);
    }

    @Override
    public void incrementarUsuariosCadastrados() {
        usuariosCounter.increment();
    }

    @Override
    public void incrementarLojasCadastradas() {
        lojasCounter.increment();
    }

    @Override
    public void incrementarProdutosCadastrados() {
        produtosCounter.increment();
    }

    @Override
    public void incrementarPedidosCriados() {
        pedidosCounter.increment();
    }

    @Override
    public void incrementarCorridasCriadas() {
        corridasCounter.increment();
    }
}
