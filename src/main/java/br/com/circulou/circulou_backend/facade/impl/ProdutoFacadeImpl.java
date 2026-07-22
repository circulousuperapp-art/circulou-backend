package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.ProdutoRequestDTO;
import br.com.circulou.circulou_backend.dto.ProdutoResponseDTO;
import br.com.circulou.circulou_backend.mapper.ProdutoMapper;
import br.com.circulou.circulou_backend.model.Produto;
import br.com.circulou.circulou_backend.port.in.ProdutoUseCase;
import br.com.circulou.circulou_backend.service.ProdutoService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class ProdutoFacadeImpl implements ProdutoUseCase {

    private final ProdutoService produtoService;
    private final ProdutoMapper produtoMapper;

    public ProdutoFacadeImpl(ProdutoService produtoService, 
                             ProdutoMapper produtoMapper) {
        this.produtoService = produtoService;
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
    @Transactional
    public ProdutoResponseDTO salvar(ProdutoRequestDTO dto) {
        Produto produto = produtoMapper.toEntity(dto);
        Produto produtoSalvo = produtoService.salvar(produto);
        return produtoMapper.toResponseDTO(produtoSalvo);
    }

    @Override
    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = produtoService.buscarPorId(id);
        produtoMapper.updateEntityFromDto(produto, dto);
        Produto produtoAtualizado = produtoService.atualizar(id, produto);
        return produtoMapper.toResponseDTO(produtoAtualizado);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        produtoService.deletar(id);
    }
}
