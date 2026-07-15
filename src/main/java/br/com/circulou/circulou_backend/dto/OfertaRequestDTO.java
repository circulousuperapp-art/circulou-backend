package br.com.circulou.circulou_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação ou atualização de uma oferta comercial")
public class OfertaRequestDTO {

    @NotNull
    @Schema(description = "ID da loja", example = "1")
    private Long lojaId;

    @NotNull
    @Schema(description = "ID do produto", example = "1")
    private Long produtoId;

    @NotNull
    @Positive
    @Schema(description = "Preço de venda", example = "10.50")
    private BigDecimal preco;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Quantidade em estoque", example = "100")
    private Integer estoque;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Quantidade mínima recomendada em estoque", example = "10")
    private Integer estoqueMinimo;

    @Schema(description = "Status de atividade da oferta", example = "true")
    private Boolean ativo;

    @Schema(description = "Indica se a oferta está disponível para venda", example = "true")
    private Boolean disponivel;

    @Schema(description = "Indica se permite retirada na loja", example = "false")
    private Boolean permiteRetirada;

    @Schema(description = "Indica se permite entrega", example = "true")
    private Boolean permiteEntrega;
}
