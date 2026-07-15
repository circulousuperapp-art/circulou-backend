package br.com.circulou.circulou_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para solicitação de ativação ou atualização do perfil de lojista")
public class LojistaProfileRequestDTO {

    @NotBlank
    @Size(max = 20)
    @Schema(description = "CNPJ da loja", example = "12345678000199")
    private String cnpj;

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Razão Social", example = "Circulou Ltda")
    private String razaoSocial;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Segmento de atuação", example = "Alimentos")
    private String segmento;
}
