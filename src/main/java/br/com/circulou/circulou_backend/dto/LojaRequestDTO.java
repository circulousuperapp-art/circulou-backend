package br.com.circulou.circulou_backend.dto;

import br.com.circulou.circulou_backend.model.Endereco;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação ou atualização de uma loja")
public class LojaRequestDTO {

    @NotBlank
    @Schema(description = "Nome fantasia da loja", example = "Burger House")
    private String nome;

    @NotBlank
    @Email
    @Schema(description = "E-mail de contato e login", example = "contato@burgerhouse.com")
    private String email;

    @NotBlank
    @Size(min = 6)
    @Schema(description = "Senha de acesso (mínimo 6 caracteres)", example = "admin123")
    private String senha;

    @NotBlank
    @Schema(description = "Telefone comercial", example = "1144445555")
    private String telefone;

    @Schema(description = "URL do logotipo", example = "http://imagem.com/logo.jpg")
    private String logo;

    @NotNull
    @Positive
    @Schema(description = "Tempo médio de preparo em minutos", example = "30")
    private Integer tempoMedioPreparo;

    @Schema(description = "Status de atividade da loja", example = "true")
    private Boolean ativa;

    @Schema(description = "Indica se a loja possui selo de confiança", example = "true")
    private Boolean seloConfianca;

    @Schema(description = "Endereço físico da loja")
    private Endereco endereco;
}
