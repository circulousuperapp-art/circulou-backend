package br.com.circulou.circulou_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação de um pedido")
public class PedidoRequestDTO {

    @NotNull
    @Schema(description = "ID do usuário solicitante", example = "1")
    private Long usuarioId;

    @NotNull
    @Schema(description = "ID da loja destino", example = "1")
    private Long lojaId;

    @NotNull
    @Schema(description = "Lista de itens do pedido")
    private List<ItemPedidoSimplesDTO> itens;

}
