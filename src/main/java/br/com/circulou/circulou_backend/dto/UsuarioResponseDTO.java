package br.com.circulou.circulou_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de retorno de um usuário")
public class UsuarioResponseDTO {

    @Schema(description = "Identificador único do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome completo", example = "João Silva")
    private String nome;

    @Schema(description = "E-mail cadastrado", example = "joao@email.com")
    private String email;

    @Schema(description = "Perfil de acesso", example = "USER")
    private String role;

    @Schema(description = "Telefone de contato", example = "11999999999")
    private String telefone;

    @Schema(description = "URL da foto de perfil", example = "http://imagem.com/foto.jpg")
    private String fotoPerfil;

    @Schema(description = "Indica se o usuário está ativo", example = "true")
    private Boolean ativo;
}
