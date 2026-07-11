package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.dto.ErrorResponseDTO;
import br.com.circulou.circulou_backend.dto.LojaRequestDTO;
import br.com.circulou.circulou_backend.dto.LojaResponseDTO;
import br.com.circulou.circulou_backend.port.in.LojaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lojas")
@Tag(name = "Lojas", description = "Operações relacionadas às lojas (parceiros)")
public class LojaController {

    private final LojaUseCase lojaUseCase;

    public LojaController(LojaUseCase lojaUseCase) {
        this.lojaUseCase = lojaUseCase;
    }

    @PostMapping
    @Operation(summary = "Cadastrar uma nova loja", description = "Registra uma nova loja parceira no Circulou Super App")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loja criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public LojaResponseDTO criarLoja(@Valid @RequestBody LojaRequestDTO lojaDTO) {
        return lojaUseCase.salvar(lojaDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todas as lojas", description = "Retorna uma lista de todas as lojas parceiras")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public List<LojaResponseDTO> listarLojas() {
        return lojaUseCase.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar loja por ID", description = "Retorna os detalhes de uma loja específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loja encontrada"),
        @ApiResponse(responseCode = "404", description = "Loja não encontrada", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public LojaResponseDTO buscarLoja(@PathVariable Long id) {
        return lojaUseCase.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma loja", description = "Remove uma loja do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loja deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Loja não encontrada", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public void deletarLoja(@PathVariable Long id) {
        lojaUseCase.deletar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma loja", description = "Atualiza os dados cadastrais de uma loja existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loja atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Loja não encontrada", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public LojaResponseDTO atualizarLoja(@PathVariable Long id, @Valid @RequestBody LojaRequestDTO lojaDTO) {
        return lojaUseCase.atualizar(id, lojaDTO);
    }
}
