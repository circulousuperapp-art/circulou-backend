package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.dto.ErrorResponseDTO;
import br.com.circulou.circulou_backend.dto.UsuarioRequestDTO;
import br.com.circulou.circulou_backend.dto.UsuarioResponseDTO;
import br.com.circulou.circulou_backend.port.in.UsuarioUseCase;
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
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Operações relacionadas aos usuários (CRUD)")
public class UsuarioController {

    private final UsuarioUseCase usuarioUseCase;

    public UsuarioController(UsuarioUseCase usuarioUseCase) {
        this.usuarioUseCase = usuarioUseCase;
    }

    @PostMapping
    @Operation(summary = "Criar um novo usuário", description = "Cadastra um novo usuário no sistema com senha criptografada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public UsuarioResponseDTO salvarUsuario(@Valid @RequestBody UsuarioRequestDTO usuarioDTO) {
        return usuarioUseCase.salvar(usuarioDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista de todos os usuários cadastrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioUseCase.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes de um usuário específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public UsuarioResponseDTO buscarUsuarioPorId(@PathVariable Long id) {
        return usuarioUseCase.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um usuário", description = "Remove um usuário do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public void deletarUsuario(@PathVariable Long id) {
        usuarioUseCase.deletar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public UsuarioResponseDTO atualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO usuarioDTO) {
        return usuarioUseCase.atualizar(id, usuarioDTO);
    }
}


