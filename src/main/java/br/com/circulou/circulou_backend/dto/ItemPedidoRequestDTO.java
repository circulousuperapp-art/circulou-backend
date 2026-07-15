package br.com.circulou.circulou_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Dados para criação ou atualização de um item de pedido")
public class ItemPedidoRequestDTO {

    @NotNull
    @Positive
    @Schema(description = "Quantidade do produto", example = "2")
    private Integer quantidade;

    @NotNull
    @Schema(description = "ID do pedido associado", example = "1")
    private Long pedidoId;

    @NotNull
    @Schema(description = "ID da oferta associada", example = "1")
    private Long ofertaId;
}
