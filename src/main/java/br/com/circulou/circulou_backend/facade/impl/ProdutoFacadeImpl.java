package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.ProdutoRequestDTO;
import br.com.circulou.circulou_backend.dto.ProdutoResponseDTO;
import br.com.circulou.circulou_backend.mapper.ProdutoMapper;
import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.model.Produto;
import br.com.circulou.circulou_backend.port.in.ProdutoUseCase;
import br.com.circulou.circulou_backend.service.LojaService;
import br.com.circulou.circulou_backend.service.ProdutoService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProdutoFacadeImpl implements ProdutoUseCase {

    private final ProdutoService produtoService;
    private final LojaService lojaService;
    private final ProdutoMapper produtoMapper;

    public ProdutoFacadeImpl(ProdutoService produtoService, 
                             LojaService lojaService,
                             ProdutoMapper produtoMapper) {
        this.produtoService = produtoService;
        this.lojaService = lojaService;
        this.produtoMapper = produtoMapper;
    }

    @Override
    public List<ProdutoResponseDTO> listarTodos() {
        return produtoService.listarTodos()
                .stream()
                .map(produtoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoService.buscarPorId(id);
        return produtoMapper.toResponseDTO(produto);
    }

    @Override
    public ProdutoResponseDTO salvar(ProdutoRequestDTO dto) {
        Produto produto = produtoMapper.toEntity(dto);
        vincularLoja(produto, dto.getLojaId());

        Produto produtoSalvo = produtoService.salvar(produto);
        return produtoMapper.toResponseDTO(produtoSalvo);
    }

    @Override
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = produtoService.buscarPorId(id);

        produtoMapper.updateEntityFromDto(produto, dto);
        vincularLoja(produto, dto.getLojaId());

        Produto produtoAtualizado = produtoService.atualizar(id, produto);
        return produtoMapper.toResponseDTO(produtoAtualizado);
    }

    @Override
    public void deletar(Long id) {
        produtoService.deletar(id);
    }

    private void vincularLoja(Produto produto, Long lojaId) {
        if (lojaId != null) {
            Loja loja = lojaService.buscarPorId(lojaId);
            produto.setLoja(loja);
        }
    }
}
