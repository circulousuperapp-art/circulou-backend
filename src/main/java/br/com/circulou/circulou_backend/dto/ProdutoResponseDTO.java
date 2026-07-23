package br.com.circulou.circulou_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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

    @Schema(description = "Marca do produto", example = "Seara")
    private String marca;

    @Schema(description = "Unidade de medida", example = "kg")
    private String unidadeMedida;

    @Schema(description = "Peso do produto", example = "1.500")
    private BigDecimal peso;

    @Schema(description = "Código de barras", example = "7891234567890")
    private String codigoBarras;

    @Schema(description = "URL da imagem principal", example = "http://imagem.com/produto.jpg")
    private String imagemPrincipal;

    @Schema(description = "Indica se o produto está ativo", example = "true")
    private Boolean ativo;
}
