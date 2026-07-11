package br.com.circulou.circulou_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Objeto padrão para respostas de erro da API")
public class ErrorResponseDTO {

    @Schema(description = "Código de status HTTP", example = "400")
    private Integer status;

    @Schema(description = "Categoria do erro", example = "Dados inválidos")
    private String erro;

    @Schema(description = "Mensagem detalhada do erro", example = "Um ou mais campos apresentam erros de validação")
    private String mensagem;

    @Schema(description = "Nome do campo relacionado ao erro (quando aplicável)", example = "email")
    private String campo;

    @Schema(description = "Data e hora da ocorrência", example = "2026-06-24T21:36:00")
    private LocalDateTime timestamp;

    @Schema(description = "Lista detalhada de erros de validação de campos")
    private List<ValidationError> erros;

    @Getter
    @AllArgsConstructor
    @Schema(description = "Detalhe de erro de validação em um campo específico")
    public static class ValidationError {

        @Schema(description = "Nome do campo que falhou na validação", example = "email")
        private String campo;

        @Schema(description = "Mensagem de erro da validação", example = "não deve estar em branco")
        private String mensagem;
    }
}
