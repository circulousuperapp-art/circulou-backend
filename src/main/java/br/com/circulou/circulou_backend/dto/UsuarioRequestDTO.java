package br.com.circulou.circulou_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
@Schema(description = "Dados para criação ou atualização de um usuário")
public class UsuarioRequestDTO {

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Nome completo do usuário", example = "João Silva")
    private String nome;

    @NotBlank
    @Email
    @Size(max = 255)
    @Schema(description = "E-mail único do usuário", example = "joao@email.com")
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    @Schema(description = "Senha de acesso (mínimo 6 caracteres)", example = "senha123")
    private String senha;

    @Size(max = 255)
    @Schema(description = "Perfil de acesso", example = "USER")
    private String role;

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Telefone de contato", example = "11999999999")
    private String telefone;

    @Size(max = 255)
    @Schema(description = "URL da foto de perfil", example = "http://imagem.com/foto.jpg")
    private String fotoPerfil;

    @Schema(description = "Status de atividade do usuário", example = "true")
    private Boolean ativo;
}
