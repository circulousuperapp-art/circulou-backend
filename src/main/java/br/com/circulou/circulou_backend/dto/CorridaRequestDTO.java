package br.com.circulou.circulou_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para solicitação de uma corrida")
public class CorridaRequestDTO {

    @NotNull
    @Schema(description = "ID do cliente solicitante", example = "1")
    private Long clienteId;

    @Schema(description = "ID do motorista (opcional na solicitação)", example = "1")
    private Long motoristaId;

    @Schema(description = "Logradouro de origem", example = "Rua das Flores")
    private String origemLogradouro;

    @Schema(description = "Número de origem", example = "123")
    private String origemNumero;

    @Schema(description = "Bairro de origem", example = "Centro")
    private String origemBairro;

    @Schema(description = "Cidade de origem", example = "São Paulo")
    private String origemCidade;

    @Schema(description = "Estado de origem", example = "SP")
    private String origemEstado;

    @Schema(description = "Latitude de origem")
    private Double origemLatitude;

    @Schema(description = "Longitude de origem")
    private Double origemLongitude;

    @Schema(description = "Logradouro de destino", example = "Av. Paulista")
    private String destinoLogradouro;

    @Schema(description = "Número de destino", example = "1000")
    private String destinoNumero;

    @Schema(description = "Bairro de destino", example = "Bela Vista")
    private String destinoBairro;

    @Schema(description = "Cidade de destino", example = "São Paulo")
    private String destinoCidade;

    @Schema(description = "Estado de destino", example = "SP")
    private String destinoEstado;

    @Schema(description = "Latitude de destino")
    private Double destinoLatitude;

    @Schema(description = "Longitude de destino")
    private Double destinoLongitude;

    @Schema(description = "Valor estimado da corrida", example = "25.50")
    private BigDecimal valorEstimado;
}
