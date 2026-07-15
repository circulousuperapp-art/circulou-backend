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
@Schema(description = "Dados de resposta do perfil de lojista")
public class LojistaProfileResponseDTO {

    @Schema(description = "Identificador único do perfil", example = "1")
    private Long id;

    @Schema(description = "ID do usuário associado", example = "1")
    private Long usuarioId;

    @Schema(description = "Status atual do perfil", example = "ATIVO")
    private StatusPerfil statusPerfil;

    @Schema(description = "CNPJ da loja", example = "12345678000199")
    private String cnpj;

    @Schema(description = "Razão Social", example = "Circulou Ltda")
    private String razaoSocial;

    @Schema(description = "Segmento de atuação", example = "Alimentos")
    private String segmento;

    @Schema(description = "Status da documentação", example = "APROVADO")
    private StatusDocumentacao statusDocumentacao;

    @Schema(description = "Média de avaliação do lojista", example = "4.90")
    private BigDecimal ratingMedia;
}
