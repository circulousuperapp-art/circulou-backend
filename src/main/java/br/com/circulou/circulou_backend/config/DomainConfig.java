package br.com.circulou.circulou_backend.config;

import br.com.circulou.circulou_backend.model.PedidoStatusPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public PedidoStatusPolicy pedidoStatusPolicy() {
        return new PedidoStatusPolicy();
    }
}
