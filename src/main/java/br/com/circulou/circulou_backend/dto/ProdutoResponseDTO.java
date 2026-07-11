package br.com.circulou.circulou_backend.dto;

import br.com.circulou.circulou_backend.model.Loja;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de retorno de um produto")
public class ProdutoResponseDTO {

    @Schema(description = "Identificador único do produto", example = "1")
    private Long id;

    @Schema(description = "Nome do produto", example = "Hambúrguer Artesanal")
    private String nome;

    @Schema(description = "Descrição detalhada", example = "Delicioso hambúrguer com queijo e bacon")
    private String descricao;

    @Schema(description = "Preço unitário", example = "35.50")
    private Double preco;

    @Schema(description = "Quantidade em estoque", example = "10")
    private Integer estoque;

    @Schema(description = "Categoria do produto", example = "Lanches")
    private String categoria;

    @Schema(description = "URL da imagem", example = "http://imagem.com/produto.jpg")
    private String imagem;

    @Schema(description = "Indica se o produto está ativo", example = "true")
    private Boolean ativo;

    @Schema(description = "Dados da loja proprietária")
    private Loja loja;
}
