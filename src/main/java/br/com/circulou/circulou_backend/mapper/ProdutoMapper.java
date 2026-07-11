package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.ProdutoRequestDTO;
import br.com.circulou.circulou_backend.dto.ProdutoResponseDTO;
import br.com.circulou.circulou_backend.model.Produto;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {

    public ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getEstoque(),
                produto.getCategoria(),
                produto.getImagem(),
                produto.getAtivo(),
                produto.getLoja()
        );
    }

    public Produto toEntity(ProdutoRequestDTO dto) {
        Produto produto = new Produto();
        updateEntityFromDto(produto, dto);
        return produto;
    }

    public void updateEntityFromDto(Produto produto, ProdutoRequestDTO dto) {
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setEstoque(dto.getEstoque());
        produto.setCategoria(dto.getCategoria());
        produto.setImagem(dto.getImagem());
        produto.setAtivo(dto.getAtivo());
    }
}
