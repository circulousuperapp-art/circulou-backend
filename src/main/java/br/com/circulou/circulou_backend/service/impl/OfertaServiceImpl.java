package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.exception.BusinessException;
import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.Oferta;
import br.com.circulou.circulou_backend.port.out.OfertaRepositoryPort;
import br.com.circulou.circulou_backend.service.OfertaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OfertaServiceImpl implements OfertaService {

    private final OfertaRepositoryPort repositoryPort;

    public OfertaServiceImpl(OfertaRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public List<Oferta> listarTodas() {
        return repositoryPort.findAll();
    }

    @Override
    public Oferta buscarPorId(Long id) {
        return repositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oferta não encontrada"));
    }

    @Override
    public List<Oferta> listarPorLoja(Long lojaId) {
        return repositoryPort.findByLojaId(lojaId);
    }

    @Override
    public List<Oferta> listarPorProduto(Long produtoId) {
        return repositoryPort.findByProdutoId(produtoId);
    }

    @Override
    @Transactional
    public Oferta salvar(Oferta oferta) {
        validarRegrasDeNegocio(oferta);
        validarUnicidade(oferta);
        return repositoryPort.save(oferta);
    }

    @Override
    @Transactional
    public Oferta atualizar(Long id, Oferta oferta) {
        Oferta existente = buscarPorId(id);
        oferta.setId(id);
        
        validarRegrasDeNegocio(oferta);
        
        // Se mudou a combinação Loja/Produto, valida unicidade novamente
        if (!existente.getLoja().getId().equals(oferta.getLoja().getId()) || 
            !existente.getProduto().getId().equals(oferta.getProduto().getId())) {
            validarUnicidade(oferta);
        }
        
        return repositoryPort.save(oferta);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        Oferta oferta = buscarPorId(id);
        oferta.setAtivo(false);
        repositoryPort.save(oferta);
    }

    @Override
    @Transactional
    public void registrarVenda(Long ofertaId, Integer quantidade) {
        Oferta oferta = buscarPorId(ofertaId);
        
        if (oferta.getEstoque() < quantidade) {
            throw new BusinessException("Estoque insuficiente para a oferta do produto " + oferta.getProduto().getNome());
        }

        oferta.setEstoque(oferta.getEstoque() - quantidade);
        
        if (oferta.getEstoque() == 0) {
            oferta.setDisponivel(false);
        }
        
        repositoryPort.save(oferta);
    }

    private void validarRegrasDeNegocio(Oferta oferta) {
        if (oferta.getPreco() == null || oferta.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Preço deve ser obrigatório e maior que zero");
        }

        if (oferta.getEstoque() == null || oferta.getEstoque() < 0) {
            throw new BusinessException("Estoque não pode ser negativo");
        }

        if (oferta.getProduto() == null || Boolean.FALSE.equals(oferta.getProduto().getAtivo())) {
            throw new BusinessException("O produto deve estar obrigatoriamente ativo para criar uma oferta");
        }

        if (oferta.getLoja() == null || Boolean.FALSE.equals(oferta.getLoja().getAtiva())) {
            throw new BusinessException("A loja deve estar obrigatoriamente ativa para criar uma oferta");
        }
    }

    private void validarUnicidade(Oferta oferta) {
        repositoryPort.findByLojaIdAndProdutoId(oferta.getLoja().getId(), oferta.getProduto().getId())
                .ifPresent(o -> {
                    throw new BusinessException("Já existe uma oferta ativa para esta combinação de Loja e Produto");
                });
    }
}
