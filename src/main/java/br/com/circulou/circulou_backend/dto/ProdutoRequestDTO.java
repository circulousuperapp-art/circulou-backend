package br.com.circulou.circulou_backend.dto;

import br.com.circulou.circulou_backend.model.Loja;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação ou atualização de um produto")
public class ProdutoRequestDTO {

    @NotBlank
    @Schema(description = "Nome do produto", example = "Hambúrguer Artesanal")
    private String nome;

    @NotBlank
    @Schema(description = "Descrição detalhada", example = "Delicioso hambúrguer com queijo e bacon")
    private String descricao;

    @NotNull
    @Positive
    @Schema(description = "Preço unitário", example = "35.50")
    private Double preco;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Quantidade em estoque", example = "10")
    private Integer estoque;

    @NotBlank
    @Schema(description = "Categoria do produto", example = "Lanches")
    private String categoria;

    @Schema(description = "URL da imagem do produto", example = "http://imagem.com/produto.jpg")
    private String imagem;

    @Schema(description = "Status de atividade do produto", example = "true")
    private Boolean ativo;

    @Schema(description = "ID da loja proprietária", example = "1")
    private Long lojaId;
}
