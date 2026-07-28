package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.Produto;
import br.com.circulou.circulou_backend.port.out.MetricsPort;
import br.com.circulou.circulou_backend.port.out.ProdutoRepositoryPort;
import br.com.circulou.circulou_backend.service.ProdutoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepositoryPort produtoRepositoryPort;
    private final MetricsPort metricsPort;

    public ProdutoServiceImpl(ProdutoRepositoryPort produtoRepositoryPort, MetricsPort metricsPort) {
        this.produtoRepositoryPort = produtoRepositoryPort;
        this.metricsPort = metricsPort;
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
        Produto salvo = produtoRepositoryPort.save(produto);
        metricsPort.incrementarProdutosCadastrados();
        return salvo;
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
        Produto produto = buscarEntidadePorId(id);
        produto.setAtivo(false);
        produtoRepositoryPort.save(produto);
    }

    private Produto buscarEntidadePorId(Long id) {
        return produtoRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
    }
}
