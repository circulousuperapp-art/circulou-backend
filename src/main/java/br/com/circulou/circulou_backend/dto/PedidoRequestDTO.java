package br.com.circulou.circulou_backend.dto;

import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação ou atualização de um pedido")
public class PedidoRequestDTO {

    @NotNull
    @Positive
    @Schema(description = "Valor total do pedido", example = "150.75")
    private Double valorTotal;

    @NotBlank
    @Schema(description = "Status atual do pedido", example = "PENDENTE")
    private String status;

    @Schema(description = "Data de criação do pedido")
    private LocalDateTime dataCriacao;

    @Schema(description = "ID do usuário solicitante", example = "1")
    private Long usuarioId;

    @Schema(description = "ID da loja destino", example = "1")
    private Long lojaId;

}
