package br.com.circulou.circulou_backend.service;

import br.com.circulou.circulou_backend.model.Pedido;
import br.com.circulou.circulou_backend.model.PedidoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PedidoService {
    List<Pedido> listarTodos();
    Page<Pedido> listarTodos(Pageable pageable);
    Pedido buscarPorId(Long id);
    Pedido salvar(Pedido pedido);
    Pedido atualizar(Long id, Pedido pedido);
    void deletar(Long id);
    void alterarStatus(Long id, PedidoStatus novoStatus);
    void cancelar(Long id);
}
