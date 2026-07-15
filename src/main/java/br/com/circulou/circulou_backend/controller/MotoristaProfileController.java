package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.dto.ErrorResponseDTO;
import br.com.circulou.circulou_backend.dto.MotoristaProfileRequestDTO;
import br.com.circulou.circulou_backend.dto.MotoristaProfileResponseDTO;
import br.com.circulou.circulou_backend.facade.MotoristaProfileFacade;
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
@RequestMapping("/motoristas")
@Tag(name = "Perfis de Motorista", description = "Operações relacionadas aos perfis de motorista no Super App")
public class MotoristaProfileController {

    private final MotoristaProfileFacade motoristaProfileFacade;

    public MotoristaProfileController(MotoristaProfileFacade motoristaProfileFacade) {
        this.motoristaProfileFacade = motoristaProfileFacade;
    }

    @GetMapping
    @Operation(summary = "Listar todos os motoristas", description = "Retorna uma lista contendo todos os perfis de motorista cadastrados no sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<MotoristaProfileResponseDTO>> listarTodos() {
        return ResponseEntity.ok(motoristaProfileFacade.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar motorista por ID", description = "Retorna os detalhes detalhados de um perfil de motorista específico através de seu identificador único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<MotoristaProfileResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(motoristaProfileFacade.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Ativar perfil de motorista", description = "Cria e vincula um novo perfil de motorista ao usuário autenticado na sessão. O perfil inicia em análise documental.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Perfil criado e ativado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida ou usuário já possui perfil de motorista ativo", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<MotoristaProfileResponseDTO> salvar(@Valid @RequestBody MotoristaProfileRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(motoristaProfileFacade.salvar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar perfil de motorista", description = "Atualiza campos permitidos (CNH e Categoria) de um perfil de motorista existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados fornecidos são inválidos", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<MotoristaProfileResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody MotoristaProfileRequestDTO dto) {
        return ResponseEntity.ok(motoristaProfileFacade.atualizar(id, dto));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar perfil de motorista", description = "Realiza a exclusão lógica do perfil, marcando seu status como INATIVO e preservando o histórico de dados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Perfil desativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Perfil não encontrado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        motoristaProfileFacade.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
