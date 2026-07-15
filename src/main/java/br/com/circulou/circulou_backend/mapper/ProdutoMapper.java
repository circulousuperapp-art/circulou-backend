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
                produto.getMarca(),
                produto.getUnidadeMedida(),
                produto.getPeso(),
                produto.getCodigoBarras(),
                produto.getImagemPrincipal(),
                produto.getAtivo()
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
        produto.setMarca(dto.getMarca());
        produto.setUnidadeMedida(dto.getUnidadeMedida());
        produto.setPeso(dto.getPeso());
        produto.setCodigoBarras(dto.getCodigoBarras());
        produto.setImagemPrincipal(dto.getImagemPrincipal());
        produto.setAtivo(dto.getAtivo());
    }
}
