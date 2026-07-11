package br.com.circulou.circulou_backend.service;

import br.com.circulou.circulou_backend.model.ItemPedido;
import java.util.List;

public interface ItemPedidoService {
    List<ItemPedido> listarTodos();
    ItemPedido buscarPorId(Long id);
    ItemPedido salvar(ItemPedido itemPedido);
    ItemPedido atualizar(Long id, ItemPedido itemPedido);
    void deletar(Long id);
}
