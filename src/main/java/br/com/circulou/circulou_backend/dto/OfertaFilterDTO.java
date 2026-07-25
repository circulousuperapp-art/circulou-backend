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
@Schema(description = "Filtros para busca de ofertas")
public class OfertaFilterDTO {

    @Schema(description = "Nome do produto ou marca", example = "Hambúrguer")
    private String busca;

    @Schema(description = "Categoria do produto", example = "Alimentos")
    private String categoria;

    @Schema(description = "Identificador da loja", example = "1")
    private Long lojaId;

    @Schema(description = "Filtrar apenas ofertas disponíveis em estoque", example = "true")
    private Boolean apenasDisponiveis = true;
}
