package br.com.circulou.circulou_backend.port.out;

public interface MetricsPort {
    void incrementarUsuariosCadastrados();
    void incrementarLojasCadastradas();
    void incrementarProdutosCadastrados();
    void incrementarPedidosCriados();
    void incrementarCorridasCriadas();
}
