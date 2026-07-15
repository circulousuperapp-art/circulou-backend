package br.com.circulou.circulou_backend.dto;

import br.com.circulou.circulou_backend.model.StatusDocumentacao;
import br.com.circulou.circulou_backend.model.StatusPerfil;
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
@Schema(description = "Dados de resposta do perfil de motorista")
public class MotoristaProfileResponseDTO {

    @Schema(description = "Identificador único do perfil", example = "1")
    private Long id;

    @Schema(description = "ID do usuário associado", example = "1")
    private Long usuarioId;

    @Schema(description = "Status atual do perfil", example = "ATIVO")
    private StatusPerfil statusPerfil;

    @Schema(description = "Número da CNH", example = "12345678900")
    private String cnh;

    @Schema(description = "Categoria da CNH", example = "B")
    private String categoriaCnh;

    @Schema(description = "Status da documentação", example = "APROVADO")
    private StatusDocumentacao statusDocumentacao;

    @Schema(description = "Média de avaliação do motorista", example = "4.85")
    private BigDecimal ratingMedia;
}
