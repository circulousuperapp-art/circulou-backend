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
@Schema(description = "Dados de retorno de um item de pedido")
public class ItemPedidoResponseDTO {

    @Schema(description = "Identificador único do item", example = "1")
    private Long id;

    @Schema(description = "Quantidade", example = "2")
    private Integer quantidade;

    @Schema(description = "Nome do produto no momento da compra", example = "Hambúrguer Artesanal")
    private String nomeProduto;

    @Schema(description = "Preço unitário registrado", example = "35.50")
    private BigDecimal precoUnitario;

    @Schema(description = "Subtotal (quantidade * preco)", example = "71.00")
    private BigDecimal subtotal;

    @Schema(description = "ID do pedido associado", example = "1")
    private Long pedidoId;

    @Schema(description = "ID da oferta associada", example = "1")
    private Long ofertaId;
}
