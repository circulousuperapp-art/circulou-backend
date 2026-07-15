package br.com.circulou.circulou_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de retorno de uma oferta comercial")
public class OfertaResponseDTO {

    @Schema(description = "Identificador único da oferta", example = "1")
    private Long id;

    @Schema(description = "ID da loja", example = "1")
    private Long lojaId;

    @Schema(description = "ID do produto", example = "1")
    private Long produtoId;

    @Schema(description = "Preço de venda", example = "10.50")
    private BigDecimal preco;

    @Schema(description = "Quantidade em estoque", example = "100")
    private Integer estoque;

    @Schema(description = "Quantidade mínima recomendada em estoque", example = "10")
    private Integer estoqueMinimo;

    @Schema(description = "Indica se a oferta está ativa", example = "true")
    private Boolean ativo;

    @Schema(description = "Indica se a oferta está disponível para venda", example = "true")
    private Boolean disponivel;

    @Schema(description = "Indica se permite retirada na loja", example = "false")
    private Boolean permiteRetirada;

    @Schema(description = "Indica se permite entrega", example = "true")
    private Boolean permiteEntrega;

    @Schema(description = "Data de criação")
    private LocalDateTime dataCriacao;

    @Schema(description = "Data da última atualização")
    private LocalDateTime dataAtualizacao;
}
