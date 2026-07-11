package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.dto.ErrorResponseDTO;
import br.com.circulou.circulou_backend.dto.ItemPedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.ItemPedidoResponseDTO;
import br.com.circulou.circulou_backend.port.in.ItemPedidoUseCase;
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
@RequestMapping("/itens-pedido")
@Tag(name = "Itens de Pedido", description = "Operações relacionadas aos itens individuais de cada pedido")
public class ItemPedidoController {

    private final ItemPedidoUseCase itemPedidoUseCase;

    public ItemPedidoController(ItemPedidoUseCase itemPedidoUseCase) {
        this.itemPedidoUseCase = itemPedidoUseCase;
    }

    @PostMapping
    @Operation(summary = "Adicionar item ao pedido", description = "Adiciona um produto específico a um pedido existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item adicionado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Não autenticado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pedido ou Produto não encontrados", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ItemPedidoResponseDTO criarItemPedido(@Valid @RequestBody ItemPedidoRequestDTO itemPedidoDTO) {
        return itemPedidoUseCase.salvar(itemPedidoDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todos os itens", description = "Retorna todos os itens de pedido registrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public List<ItemPedidoResponseDTO> listarItensPedido() {
        return itemPedidoUseCase.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar item por ID", description = "Retorna os detalhes de um item de pedido específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Item não encontrado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ItemPedidoResponseDTO buscarItemPedido(@PathVariable Long id) {
        return itemPedidoUseCase.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um item", description = "Remove um item específico do pedido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item removido com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Item não encontrado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public void deletarItemPedido(@PathVariable Long id) {
        itemPedidoUseCase.deletar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um item", description = "Atualiza a quantidade ou valores de um item de pedido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Não autenticado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Item, Pedido ou Produto não encontrados", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ItemPedidoResponseDTO atualizarItemPedido(@PathVariable Long id, @Valid @RequestBody ItemPedidoRequestDTO itemPedidoDTO) {
        return itemPedidoUseCase.atualizar(id, itemPedidoDTO);
    }
}
