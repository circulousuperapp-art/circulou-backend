package br.com.circulou.circulou_backend.dto;

import br.com.circulou.circulou_backend.model.JornadaParadaStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de retorno de uma parada da jornada")
public class JornadaParadaResponseDTO {

    @Schema(description = "ID da loja da parada", example = "1")
    private Long lojaId;

    @Schema(description = "ID do pedido associado à parada", example = "10")
    private Long pedidoId;

    @Schema(description = "Ordem da parada na jornada", example = "1")
    private Integer ordem;

    @Schema(description = "Status atual da parada", example = "AGUARDANDO")
    private JornadaParadaStatus status;
}
