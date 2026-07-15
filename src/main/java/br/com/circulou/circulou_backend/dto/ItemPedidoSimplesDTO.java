package br.com.circulou.circulou_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Dados simplificados de um item para criação de pedido")
public class ItemPedidoSimplesDTO {

    @NotNull
    @Positive
    @Schema(description = "Quantidade do produto", example = "2")
    private Integer quantidade;

    @NotNull
    @Schema(description = "ID da oferta associada", example = "1")
    private Long ofertaId;
}
