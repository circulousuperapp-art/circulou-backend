package br.com.circulou.circulou_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de retorno de um item de pedido")
public class ItemPedidoResponseDTO {

    @Schema(description = "Identificador único do item", example = "1")
    private Long id;

    @Schema(description = "Quantidade", example = "2")
    private Integer quantidade;

    @Schema(description = "Preço unitário registrado", example = "35.50")
    private Double precoUnitario;

    @Schema(description = "Subtotal (quantidade * preco)", example = "71.00")
    private Double subtotal;

    @Schema(description = "ID do pedido associado", example = "1")
    private Long pedidoId;

    @Schema(description = "ID do produto associado", example = "1")
    private Long produtoId;
}
