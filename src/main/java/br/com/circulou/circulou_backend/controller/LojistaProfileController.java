package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.dto.ErrorResponseDTO;
import br.com.circulou.circulou_backend.dto.LojistaProfileRequestDTO;
import br.com.circulou.circulou_backend.dto.LojistaProfileResponseDTO;
import br.com.circulou.circulou_backend.facade.LojistaProfileFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lojistas")
@Tag(name = "Perfis de Lojista", description = "Operações relacionadas aos perfis de lojista no Super App")
public class LojistaProfileController {

    private final LojistaProfileFacade lojistaProfileFacade;

    public LojistaProfileController(LojistaProfileFacade lojistaProfileFacade) {
        this.lojistaProfileFacade = lojistaProfileFacade;
    }

    @GetMapping
    @Operation(summary = "Listar todos os lojistas", description = "Retorna uma lista contendo todos os perfis de lojista cadastrados no sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<LojistaProfileResponseDTO>> listarTodos() {
        return ResponseEntity.ok(lojistaProfileFacade.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar lojista por ID", description = "Retorna os detalhes detalhados de um perfil de lojista específico através de seu identificador único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<LojistaProfileResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(lojistaProfileFacade.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Ativar perfil de lojista", description = "Cria e vincula um novo perfil de lojista ao usuário autenticado na sessão. O perfil inicia em análise documental.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Perfil criado e ativado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida ou usuário já possui perfil de lojista ativo", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<LojistaProfileResponseDTO> salvar(@Valid @RequestBody LojistaProfileRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lojistaProfileFacade.salvar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar perfil de lojista", description = "Atualiza os dados comerciais (CNPJ, Razão Social e Segmento) de um perfil de lojista existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados fornecidos são inválidos", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<LojistaProfileResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody LojistaProfileRequestDTO dto) {
        return ResponseEntity.ok(lojistaProfileFacade.atualizar(id, dto));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar perfil de lojista", description = "Realiza a exclusão lógica do perfil, marcando seu status como INATIVO e preservando o histórico comercial.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Perfil desativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        lojistaProfileFacade.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
