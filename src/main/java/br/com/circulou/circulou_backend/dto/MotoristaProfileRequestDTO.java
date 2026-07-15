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
@Schema(description = "Dados para solicitação de ativação ou atualização do perfil de motorista")
public class MotoristaProfileRequestDTO {

    @NotBlank
    @Size(max = 20)
    @Schema(description = "Número da CNH", example = "12345678900")
    private String cnh;

    @NotBlank
    @Size(max = 10)
    @Schema(description = "Categoria da CNH", example = "B")
    private String categoriaCnh;
}
