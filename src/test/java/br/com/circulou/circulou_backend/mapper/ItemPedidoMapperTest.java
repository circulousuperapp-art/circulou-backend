package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.ItemPedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.ItemPedidoResponseDTO;
import br.com.circulou.circulou_backend.dto.ItemPedidoSimplesDTO;
import br.com.circulou.circulou_backend.model.ItemPedido;
import br.com.circulou.circulou_backend.model.Oferta;
import br.com.circulou.circulou_backend.model.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

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

        Oferta oferta = new Oferta();
        oferta.setId(20L);

        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setId(1L);
        itemPedido.setQuantidade(2);
        itemPedido.setPrecoUnitario(new BigDecimal("50.00"));
        itemPedido.setSubtotal(new BigDecimal("100.00"));
        itemPedido.setPedido(pedido);
        itemPedido.setOferta(oferta);
        itemPedido.setNomeProduto("Produto Teste");

        // When
        ItemPedidoResponseDTO responseDTO = mapper.toResponseDTO(itemPedido);

        // Then
        assertNotNull(responseDTO);
        assertEquals(itemPedido.getId(), responseDTO.getId());
        assertEquals(itemPedido.getQuantidade(), responseDTO.getQuantidade());
        assertEquals(itemPedido.getPrecoUnitario(), responseDTO.getPrecoUnitario());
        assertEquals(itemPedido.getSubtotal(), responseDTO.getSubtotal());
        assertEquals(pedido.getId(), responseDTO.getPedidoId());
        assertEquals(oferta.getId(), responseDTO.getOfertaId());
        assertEquals("Produto Teste", responseDTO.getNomeProduto());
    }

    @Test
    void toResponseDTO_WithNullRelations_ShouldMapNullIds() {
        // Given
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setId(1L);
        itemPedido.setPedido(null);
        itemPedido.setOferta(null);

        // When
        ItemPedidoResponseDTO responseDTO = mapper.toResponseDTO(itemPedido);

        // Then
        assertNotNull(responseDTO);
        assertNull(responseDTO.getPedidoId());
        assertNull(responseDTO.getOfertaId());
    }

    @Test
    void toEntityFromSimples_ShouldMapCorrectly() {
        // Given
        ItemPedidoSimplesDTO simplesDTO = new ItemPedidoSimplesDTO(3, 20L);

        // When
        ItemPedido itemPedido = mapper.toEntity(simplesDTO);

        // Then
        assertNotNull(itemPedido);
        assertEquals(simplesDTO.getQuantidade(), itemPedido.getQuantidade());
        assertNull(itemPedido.getPedido());
        assertNull(itemPedido.getOferta());
    }

    @Test
    void toEntity_ShouldMapCorrectly() {
        // Given
        ItemPedidoRequestDTO requestDTO = new ItemPedidoRequestDTO();
        requestDTO.setQuantidade(3);
        requestDTO.setPedidoId(10L);
        requestDTO.setOfertaId(20L);

        // When
        ItemPedido itemPedido = mapper.toEntity(requestDTO);

        // Then
        assertNotNull(itemPedido);
        assertEquals(requestDTO.getQuantidade(), itemPedido.getQuantidade());
        assertNull(itemPedido.getPedido());
        assertNull(itemPedido.getOferta());
    }

    @Test
    void updateEntityFromDto_ShouldUpdateFields() {
        // Given
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setQuantidade(1);

        ItemPedidoRequestDTO requestDTO = new ItemPedidoRequestDTO();
        requestDTO.setQuantidade(5);

        // When
        mapper.updateEntityFromDto(itemPedido, requestDTO);

        // Then
        assertEquals(requestDTO.getQuantidade(), itemPedido.getQuantidade());
    }
}
