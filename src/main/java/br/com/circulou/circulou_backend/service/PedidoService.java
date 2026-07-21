package br.com.circulou.circulou_backend.service;

import br.com.circulou.circulou_backend.model.Pedido;
import br.com.circulou.circulou_backend.model.PedidoStatus;

import java.util.List;

public interface PedidoService {
    List<Pedido> listarTodos();
    Pedido buscarPorId(Long id);
    Pedido salvar(Pedido pedido);
    Pedido atualizar(Long id, Pedido pedido);
    void deletar(Long id);
    void alterarStatus(Long id, PedidoStatus novoStatus);
    void cancelar(Long id);
}
