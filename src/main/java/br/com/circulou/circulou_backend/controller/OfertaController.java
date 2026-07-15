package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.dto.ErrorResponseDTO;
import br.com.circulou.circulou_backend.dto.OfertaRequestDTO;
import br.com.circulou.circulou_backend.dto.OfertaResponseDTO;
import br.com.circulou.circulou_backend.port.in.OfertaUseCase;
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
@RequestMapping("/ofertas")
@Tag(name = "Ofertas", description = "Operações relacionadas às ofertas comerciais de produtos em lojas")
public class OfertaController {

    private final OfertaUseCase ofertaUseCase;

    public OfertaController(OfertaUseCase ofertaUseCase) {
        this.ofertaUseCase = ofertaUseCase;
    }

    @PostMapping
    @Operation(summary = "Criar nova oferta", description = "Registra uma nova oferta comercial vinculando uma loja a um produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Oferta criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Não autenticado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public OfertaResponseDTO criarOferta(@Valid @RequestBody OfertaRequestDTO ofertaDTO) {
        return ofertaUseCase.salvar(ofertaDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todas as ofertas", description = "Retorna todas as ofertas comerciais cadastradas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public List<OfertaResponseDTO> listarTodas() {
        return ofertaUseCase.listarTodas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar oferta por ID", description = "Retorna os detalhes de uma oferta específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Oferta encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Oferta não encontrada", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public OfertaResponseDTO buscarPorId(@PathVariable Long id) {
        return ofertaUseCase.buscarPorId(id);
    }

    @GetMapping("/loja/{lojaId}")
    @Operation(summary = "Listar ofertas por loja", description = "Retorna todas as ofertas comerciais de uma loja específica")
    public List<OfertaResponseDTO> listarPorLoja(@PathVariable Long lojaId) {
        return ofertaUseCase.listarPorLoja(lojaId);
    }

    @GetMapping("/produto/{produtoId}")
    @Operation(summary = "Listar ofertas por produto", description = "Retorna todas as ofertas de diferentes lojas para um mesmo produto")
    public List<OfertaResponseDTO> listarPorProduto(@PathVariable Long produtoId) {
        return ofertaUseCase.listarPorProduto(produtoId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma oferta", description = "Atualiza os dados comerciais de uma oferta existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Oferta atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Oferta não encontrada", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public OfertaResponseDTO atualizarOferta(@PathVariable Long id, @Valid @RequestBody OfertaRequestDTO ofertaDTO) {
        return ofertaUseCase.atualizar(id, ofertaDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma oferta (Inativação)", description = "Realiza a inativação lógica de uma oferta no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Oferta deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Oferta não encontrada", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public void deletarOferta(@PathVariable Long id) {
        ofertaUseCase.deletar(id);
    }
}
