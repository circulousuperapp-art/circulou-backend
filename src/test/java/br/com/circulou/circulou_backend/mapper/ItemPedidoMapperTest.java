package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.ItemPedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.ItemPedidoResponseDTO;
import br.com.circulou.circulou_backend.model.ItemPedido;
import br.com.circulou.circulou_backend.model.Pedido;
import br.com.circulou.circulou_backend.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemPedidoMapperTest {

    private ItemPedidoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ItemPedidoMapper();
    }

    @Test
    void toResponseDTO_ShouldMapCorrectly() {
        // Given
        Pedido pedido = new Pedido();
        pedido.setId(10L);

        Produto produto = new Produto();
        produto.setId(20L);

        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setId(1L);
        itemPedido.setQuantidade(2);
        itemPedido.setPrecoUnitario(50.0);
        itemPedido.setSubtotal(100.0);
        itemPedido.setPedido(pedido);
        itemPedido.setProduto(produto);

        // When
        ItemPedidoResponseDTO responseDTO = mapper.toResponseDTO(itemPedido);

        // Then
        assertNotNull(responseDTO);
        assertEquals(itemPedido.getId(), responseDTO.getId());
        assertEquals(itemPedido.getQuantidade(), responseDTO.getQuantidade());
        assertEquals(itemPedido.getPrecoUnitario(), responseDTO.getPrecoUnitario());
        assertEquals(itemPedido.getSubtotal(), responseDTO.getSubtotal());
        assertEquals(pedido.getId(), responseDTO.getPedidoId());
        assertEquals(produto.getId(), responseDTO.getProdutoId());
    }

    @Test
    void toResponseDTO_WithNullRelations_ShouldMapNullIds() {
        // Given
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setId(1L);
        itemPedido.setPedido(null);
        itemPedido.setProduto(null);

        // When
        ItemPedidoResponseDTO responseDTO = mapper.toResponseDTO(itemPedido);

        // Then
        assertNotNull(responseDTO);
        assertNull(responseDTO.getPedidoId());
        assertNull(responseDTO.getProdutoId());
    }

    @Test
    void toEntity_ShouldMapCorrectly() {
        // Given
        ItemPedidoRequestDTO requestDTO = new ItemPedidoRequestDTO();
        requestDTO.setQuantidade(3);
        requestDTO.setPrecoUnitario(30.0);
        requestDTO.setSubtotal(90.0);
        requestDTO.setPedidoId(10L);
        requestDTO.setProdutoId(20L);

        // When
        ItemPedido itemPedido = mapper.toEntity(requestDTO);

        // Then
        assertNotNull(itemPedido);
        assertEquals(requestDTO.getQuantidade(), itemPedido.getQuantidade());
        assertEquals(requestDTO.getPrecoUnitario(), itemPedido.getPrecoUnitario());
        assertEquals(requestDTO.getSubtotal(), itemPedido.getSubtotal());
        // Note: Pedido and Produto are not set by the mapper directly from ID in toEntity
        assertNull(itemPedido.getPedido());
        assertNull(itemPedido.getProduto());
    }

    @Test
    void updateEntityFromDto_ShouldUpdateFields() {
        // Given
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setQuantidade(1);
        itemPedido.setPrecoUnitario(10.0);
        itemPedido.setSubtotal(10.0);

        ItemPedidoRequestDTO requestDTO = new ItemPedidoRequestDTO();
        requestDTO.setQuantidade(5);
        requestDTO.setPrecoUnitario(15.0);
        requestDTO.setSubtotal(75.0);

        // When
        mapper.updateEntityFromDto(itemPedido, requestDTO);

        // Then
        assertEquals(requestDTO.getQuantidade(), itemPedido.getQuantidade());
        assertEquals(requestDTO.getPrecoUnitario(), itemPedido.getPrecoUnitario());
        assertEquals(requestDTO.getSubtotal(), itemPedido.getSubtotal());
    }
}
