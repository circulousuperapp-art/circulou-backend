package br.com.circulou.circulou_backend.service;

import br.com.circulou.circulou_backend.model.Loja;
import java.util.List;

public interface LojaService {
    List<Loja> listarTodos();
    Loja buscarPorId(Long id);
    Loja salvar(Loja loja);
    Loja atualizar(Long id, Loja loja);
    void deletar(Long id);
}
