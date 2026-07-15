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
@Schema(description = "Dados de retorno de uma loja")
public class LojaResponseDTO {

    @Schema(description = "Identificador único da loja", example = "1")
    private Long id;

    @Schema(description = "Nome fantasia", example = "Burger House")
    private String nome;

    @Schema(description = "E-mail de contato", example = "contato@burgerhouse.com")
    private String email;

    @Schema(description = "Telefone comercial", example = "1144445555")
    private String telefone;

    @Schema(description = "URL do logotipo", example = "http://imagem.com/logo.jpg")
    private String logo;

    @Schema(description = "Tempo médio de preparo em minutos", example = "30")
    private Integer tempoMedioPreparo;

    @Schema(description = "Status de atividade da loja", example = "true")
    private Boolean ativa;

    @Schema(description = "Indica se a loja possui selo de confiança", example = "true")
    private Boolean seloConfianca;

    @Schema(description = "ID do Lojista Profile responsável", example = "1")
    private Long lojistaProfileId;
}
