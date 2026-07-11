package br.com.circulou.circulou_backend.service;

import br.com.circulou.circulou_backend.model.Usuario;
import java.util.List;

public interface UsuarioService {
    List<Usuario> listarTodos();
    Usuario buscarPorId(Long id);
    Usuario salvar(Usuario usuario);
    Usuario atualizar(Long id, Usuario usuario);
    void deletar(Long id);
}
