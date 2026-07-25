package br.com.circulou.circulou_backend.dto;

import br.com.circulou.circulou_backend.model.CorridaStatus;
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
@Schema(description = "Dados de retorno de uma corrida")
public class CorridaResponseDTO {

    @Schema(description = "Identificador único da corrida", example = "1")
    private Long id;

    @Schema(description = "ID do cliente", example = "1")
    private Long clienteId;

    @Schema(description = "ID do motorista", example = "1")
    private Long motoristaId;

    @Schema(description = "Status atual", example = "SOLICITADA")
    private CorridaStatus status;

    @Schema(description = "Data de criação")
    private LocalDateTime dataCriacao;

    @Schema(description = "Data de início")
    private LocalDateTime dataInicio;

    @Schema(description = "Data de finalização")
    private LocalDateTime dataFinalizacao;

    @Schema(description = "Valor estimado", example = "25.50")
    private BigDecimal valorEstimado;

    @Schema(description = "Valor final", example = "27.00")
    private BigDecimal valorFinal;

    @Schema(description = "Origem formatada")
    private String origem;

    @Schema(description = "Destino formatado")
    private String destino;
}
