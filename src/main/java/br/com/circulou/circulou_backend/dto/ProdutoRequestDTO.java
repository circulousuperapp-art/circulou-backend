package br.com.circulou.circulou_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotBlank
    @Schema(description = "Marca do produto", example = "Seara")
    private String marca;

    @NotBlank
    @Schema(description = "Unidade de medida", example = "kg")
    private String unidadeMedida;

    @NotNull
    @Positive
    @Schema(description = "Peso do produto", example = "1.5")
    private Double peso;

    @NotBlank
    @Schema(description = "Código de barras", example = "7891234567890")
    private String codigoBarras;

    @Schema(description = "URL da imagem principal", example = "http://imagem.com/produto.jpg")
    private String imagemPrincipal;

    @Schema(description = "Status de atividade do produto", example = "true")
    private Boolean ativo;
}
