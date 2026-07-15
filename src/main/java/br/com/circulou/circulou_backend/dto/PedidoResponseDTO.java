package br.com.circulou.circulou_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Dados de retorno de um pedido")
public class PedidoResponseDTO {

    @Schema(description = "Identificador único do pedido", example = "1")
    private Long id;

    @Schema(description = "Valor total", example = "150.75")
    private BigDecimal valorTotal;

    @Schema(description = "Status atual", example = "PENDENTE")
    private String status;

    @Schema(description = "Data e hora da criação")
    private LocalDateTime dataCriacao;

    @Schema(description = "ID do usuário solicitante", example = "1")
    private Long usuarioId;

    @Schema(description = "ID da loja destino", example = "1")
    private Long lojaId;

    @Schema(description = "Lista de itens do pedido")
    private List<ItemPedidoResponseDTO> itens;

}
