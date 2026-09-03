package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.dto.ErrorResponseDTO;
import br.com.circulou.circulou_backend.dto.JornadaResponseDTO;
import br.com.circulou.circulou_backend.facade.JornadaFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jornadas")
@Tag(name = "Jornadas", description = "Operações relacionadas ao Drive-Thru (Jornadas)")
public class JornadaController {

    private final JornadaFacade jornadaFacade;

    public JornadaController(JornadaFacade jornadaFacade) {
        this.jornadaFacade = jornadaFacade;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar jornada por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Jornada encontrada"),
        @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Jornada não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public JornadaResponseDTO buscarPorId(@PathVariable Long id) {
        return jornadaFacade.buscarMeuPorId(id);
    }

    @PatchMapping("/{id}/iniciar")
    @Operation(summary = "Iniciar jornada", description = "Move o status da jornada para EM_EXECUCAO")
    public void iniciar(@PathVariable Long id) {
        jornadaFacade.iniciar(id);
    }

    @PatchMapping("/{id}/proxima-parada")
    @Operation(summary = "Iniciar deslocamento para próxima parada", description = "Move a próxima parada aguardando para EM_TRANSITO")
    public void iniciarDeslocamento(@PathVariable Long id) {
        jornadaFacade.iniciarDeslocamentoProximaParada(id);
    }

    @PatchMapping("/{id}/chegada-loja")
    @Operation(summary = "Registrar chegada na loja", description = "Move a parada atual para NA_LOJA")
    public void registrarChegada(@PathVariable Long id) {
        jornadaFacade.registrarChegadaNaLoja(id);
    }

    @PatchMapping("/{id}/confirmar-retirada")
    @Operation(summary = "Confirmar retirada", description = "Move a parada atual para RETIRADA_REALIZADA")
    public void confirmarRetirada(@PathVariable Long id) {
        jornadaFacade.confirmarRetirada(id);
    }

    @PatchMapping("/{id}/paradas/{ordem}/cancelar")
    @Operation(summary = "Cancelar uma parada específica")
    public void cancelarParada(@PathVariable Long id, @PathVariable Integer ordem) {
        jornadaFacade.cancelarParada(id, ordem);
    }

    @PatchMapping("/{id}/concluir")
    @Operation(summary = "Concluir jornada", description = "Move o status da jornada para CONCLUIDA após todas as paradas serem terminais")
    public void concluir(@PathVariable Long id) {
        jornadaFacade.concluir(id);
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar jornada", description = "Move o status da jornada para CANCELADA")
    public void cancelar(@PathVariable Long id) {
        jornadaFacade.cancelar(id);
    }
}
