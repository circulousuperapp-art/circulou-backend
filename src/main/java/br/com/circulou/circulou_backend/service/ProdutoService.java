package br.com.circulou.circulou_backend.service;

import br.com.circulou.circulou_backend.model.Produto;
import java.util.List;

public interface ProdutoService {
    List<Produto> listarTodos();
    Produto buscarPorId(Long id);
    Produto salvar(Produto produto);
    Produto atualizar(Long id, Produto produto);
    void deletar(Long id);
}
