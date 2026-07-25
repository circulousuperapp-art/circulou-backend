package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.dto.CorridaRequestDTO;
import br.com.circulou.circulou_backend.dto.CorridaResponseDTO;
import br.com.circulou.circulou_backend.dto.ErrorResponseDTO;
import br.com.circulou.circulou_backend.port.in.CorridaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/corridas")
@Tag(name = "Corridas", description = "Operações relacionadas ao domínio de Mobilidade (Corridas)")
public class CorridaController {

    private final CorridaUseCase corridaUseCase;

    public CorridaController(CorridaUseCase corridaUseCase) {
        this.corridaUseCase = corridaUseCase;
    }

    @PostMapping
    @Operation(summary = "Solicitar nova corrida", description = "Registra uma nova solicitação de corrida no sistema")
    public CorridaResponseDTO solicitar(@Valid @RequestBody CorridaRequestDTO dto) {
        return corridaUseCase.solicitar(dto);
    }

    @GetMapping
    @Operation(summary = "Listar corridas", description = "Retorna a lista de corridas com paginação")
    public Page<CorridaResponseDTO> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return corridaUseCase.listarTodas(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar corrida por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Corrida encontrada"),
        @ApiResponse(responseCode = "404", description = "Corrida não encontrada", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public CorridaResponseDTO buscarPorId(@PathVariable Long id) {
        return corridaUseCase.buscarPorId(id);
    }

    @PatchMapping("/{id}/aceitar")
    @Operation(summary = "Aceitar corrida", description = "Motorista aceita a solicitação de corrida")
    public void aceitar(@PathVariable Long id, @RequestParam Long motoristaId) {
        corridaUseCase.aceitar(id, motoristaId);
    }

    @PatchMapping("/{id}/iniciar-caminho")
    @Operation(summary = "Iniciar caminho até o cliente", description = "Motorista informa que está a caminho do cliente")
    public void iniciarCaminho(@PathVariable Long id) {
        corridaUseCase.iniciarCaminho(id);
    }

    @PatchMapping("/{id}/embarcar")
    @Operation(summary = "Registrar embarque do cliente")
    public void embarcar(@PathVariable Long id) {
        corridaUseCase.embarcar(id);
    }

    @PatchMapping("/{id}/parada-loja")
    @Operation(summary = "Registrar parada na loja")
    public void registrarParadaLoja(@PathVariable Long id) {
        corridaUseCase.registrarParadaLoja(id);
    }

    @PatchMapping("/{id}/continuar")
    @Operation(summary = "Continuar corrida após parada")
    public void continuar(@PathVariable Long id) {
        corridaUseCase.continuarCorrida(id);
    }

    @PatchMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar corrida")
    public void finalizar(@PathVariable Long id) {
        corridaUseCase.finalizar(id);
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar corrida")
    public void cancelar(@PathVariable Long id) {
        corridaUseCase.cancelar(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar corrida (Histórico)")
    public void deletar(@PathVariable Long id) {
        corridaUseCase.deletar(id);
    }
}
