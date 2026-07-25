package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.dto.ErrorResponseDTO;
import br.com.circulou.circulou_backend.dto.PedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.PedidoResponseDTO;
import br.com.circulou.circulou_backend.port.in.PedidoUseCase;
import br.com.circulou.circulou_backend.config.PaginationProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Operações relacionadas ao gerenciamento de pedidos")
public class PedidoController {

    private final PedidoUseCase pedidoUseCase;
    private final PaginationProperties paginationProperties;

    public PedidoController(PedidoUseCase pedidoUseCase, PaginationProperties paginationProperties) {
        this.pedidoUseCase = pedidoUseCase;
        this.paginationProperties = paginationProperties;
    }

    @PostMapping
    @Operation(summary = "Criar novo pedido", description = "Registra um novo pedido de compra no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Não autenticado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Usuário ou Loja não encontrados",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public PedidoResponseDTO criarPedido(@Valid @RequestBody PedidoRequestDTO pedidoDTO) {
        return pedidoUseCase.salvar(pedidoDTO);
    }

    @GetMapping
    @Operation(summary = "Listar pedidos", description = "Retorna o histórico de pedidos com paginação opcional. Se os parâmetros 'page' e 'size' forem omitidos, o sistema retorna a lista completa respeitando o limite máximo configurado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos recuperada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado ou token inválido", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro inesperado ao processar a listagem", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<?> listarPedidos(
            @Parameter(description = "ID do usuário para filtrar o histórico") @RequestParam(required = false) Long usuarioId,
            @Parameter(description = "Número da página (0..N)") @RequestParam(required = false) Integer page,
            @Parameter(description = "Tamanho da página") @RequestParam(required = false) Integer size,
            @Parameter(description = "Ordenação (campo,asc|desc)") @RequestParam(required = false, defaultValue = "id,desc") String sort) {
        
        if (page == null && size == null) {
            if (usuarioId != null) {
                return ResponseEntity.ok(pedidoUseCase.listarPorUsuario(usuarioId));
            }
            return ResponseEntity.ok(pedidoUseCase.listarTodos());
        }

        int pageNum = (page != null) ? page : 0;
        int pageSize = (size != null) ? Math.min(size, paginationProperties.getMaxSize()) : paginationProperties.getDefaultSize();
        
        String[] sortParams = sort.split(",");
        Sort sortOrder = Sort.by(sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC, sortParams[0]);
        
        Pageable pageable = PageRequest.of(pageNum, pageSize, sortOrder);
        
        if (usuarioId != null) {
            return ResponseEntity.ok(pedidoUseCase.listarPorUsuario(usuarioId, pageable));
        }
        
        return ResponseEntity.ok(pedidoUseCase.listarTodos(pageable));
    }

    @GetMapping("/meus")
    @Operation(summary = "Listar meus pedidos", description = "Retorna o histórico de pedidos do usuário autenticado")
    public ResponseEntity<Page<PedidoResponseDTO>> listarMeusPedidos(
            @Parameter(description = "Número da página (0..N)") @RequestParam(required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Tamanho da página") @RequestParam(required = false, defaultValue = "20") Integer size,
            @Parameter(description = "Ordenação (campo,asc|desc)") @RequestParam(required = false, defaultValue = "id,desc") String sort) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(pedidoUseCase.listarMeusPedidos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna os detalhes de um pedido específico. Se o usuário não for admin, ele só pode ver seus próprios pedidos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public PedidoResponseDTO buscarPedido(@PathVariable Long id) {
        return pedidoUseCase.buscarMeuPedidoPorId(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um pedido", description = "Remove um pedido do histórico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido deletado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public void deletarPedido(@PathVariable Long id) {
        pedidoUseCase.deletar(id);
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar um pedido", description = "Cancela um pedido caso esteja dentro do prazo permitido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Prazo expirado ou status não permite cancelamento",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Não autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public void cancelarPedido(@PathVariable Long id) {
        pedidoUseCase.cancelar(id);
    }

    @PatchMapping("/{id}/aceitar")
    @Operation(summary = "Aceitar pedido", description = "Lojista aceita o pedido, movendo para status Em Preparação")
    public void aceitarPedido(@PathVariable Long id) {
        pedidoUseCase.aceitar(id);
    }

    @PatchMapping("/{id}/preparar")
    @Operation(summary = "Colocar em preparação", description = "Lojista informa que o pedido está em fase de preparação")
    public void prepararPedido(@PathVariable Long id) {
        pedidoUseCase.preparar(id);
    }

    @PatchMapping("/{id}/pronto")
    @Operation(summary = "Marcar como pronto", description = "Lojista informa que o pedido está pronto para retirada")
    public void marcarComoPronto(@PathVariable Long id) {
        pedidoUseCase.marcarComoPronto(id);
    }

    @PatchMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar atendimento do produto", description = "Lojista confirma que o produto foi entregue ao cliente")
    public void finalizarPedido(@PathVariable Long id) {
        pedidoUseCase.finalizar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um pedido", description = "Atualiza o status ou dados de um pedido existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Não autenticado", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pedido, Usuário ou Loja não encontrados",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public PedidoResponseDTO atualizarPedido(@PathVariable Long id, @Valid @RequestBody PedidoRequestDTO pedidoDTO) {
        return pedidoUseCase.atualizar(id, pedidoDTO);
    }
}
