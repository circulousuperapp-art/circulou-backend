package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.Produto;
import br.com.circulou.circulou_backend.port.out.ProdutoRepositoryPort;
import br.com.circulou.circulou_backend.service.ProdutoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepositoryPort produtoRepositoryPort;

    public ProdutoServiceImpl(ProdutoRepositoryPort produtoRepositoryPort) {
        this.produtoRepositoryPort = produtoRepositoryPort;
    }

    @Override
    public List<Produto> listarTodos() {
        return produtoRepositoryPort.findAll();
    }

    @Override
    public Produto buscarPorId(Long id) {
        return buscarEntidadePorId(id);
    }

    @Override
    @Transactional
    public Produto salvar(Produto produto) {
        return produtoRepositoryPort.save(produto);
    }

    @Override
    @Transactional
    public Produto atualizar(Long id, Produto produto) {
        buscarEntidadePorId(id);
        produto.setId(id);
        return produtoRepositoryPort.save(produto);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!produtoRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado");
        }
        produtoRepositoryPort.deleteById(id);
    }

    private Produto buscarEntidadePorId(Long id) {
        return produtoRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
    }
}
