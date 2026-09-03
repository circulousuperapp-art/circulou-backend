package br.com.circulou.circulou_backend.dto;

import br.com.circulou.circulou_backend.model.JornadaStatus;
import br.com.circulou.circulou_backend.model.JornadaTipo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de retorno de uma jornada")
public class JornadaResponseDTO {

    @Schema(description = "Identificador único da jornada", example = "1")
    private Long id;

    @Schema(description = "ID do cliente solicitante", example = "1")
    private Long clienteId;

    @Schema(description = "ID da corrida associada (apenas para MOTORISTA)", example = "200")
    private Long corridaId;

    @Schema(description = "Tipo de jornada", example = "PROPRIA")
    private JornadaTipo tipo;

    @Schema(description = "Status atual da jornada", example = "CRIADA")
    private JornadaStatus status;

    @Schema(description = "Data e hora de criação")
    private LocalDateTime createdAt;

    @Schema(description = "Data e hora da última atualização")
    private LocalDateTime updatedAt;

    @Schema(description = "Lista de paradas da jornada")
    private List<JornadaParadaResponseDTO> paradas;
}
