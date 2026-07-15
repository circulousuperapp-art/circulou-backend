package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.ItemPedidoSimplesDTO;
import br.com.circulou.circulou_backend.dto.PedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.PedidoResponseDTO;
import br.com.circulou.circulou_backend.model.ItemPedido;
import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.model.Pedido;
import br.com.circulou.circulou_backend.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoMapperTest {

    private PedidoMapper mapper;

    @Mock
    private ItemPedidoMapper itemPedidoMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new PedidoMapper(itemPedidoMapper);
    }

    @Test
    void toResponseDTO_ShouldMapCorrectly() {
        // Given
        Usuario usuario = new Usuario();
        usuario.setId(10L);

        Loja loja = new Loja();
        loja.setId(20L);

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setValorTotal(new BigDecimal("150.00"));
        pedido.setStatus("PENDENTE");
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setUsuario(usuario);
        pedido.setLoja(loja);
        pedido.setItens(new ArrayList<>());

        // When
        PedidoResponseDTO responseDTO = mapper.toResponseDTO(pedido);

        // Then
        assertNotNull(responseDTO);
        assertEquals(pedido.getId(), responseDTO.getId());
        assertEquals(pedido.getValorTotal(), responseDTO.getValorTotal());
        assertEquals(pedido.getStatus(), responseDTO.getStatus());
        assertEquals(pedido.getDataCriacao(), responseDTO.getDataCriacao());
        assertEquals(usuario.getId(), responseDTO.getUsuarioId());
        assertEquals(loja.getId(), responseDTO.getLojaId());
    }

    @Test
    void toResponseDTO_WithNullRelations_ShouldMapNullIds() {
        // Given
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(null);
        pedido.setLoja(null);
        pedido.setItens(null);

        // When
        PedidoResponseDTO responseDTO = mapper.toResponseDTO(pedido);

        // Then
        assertNotNull(responseDTO);
        assertNull(responseDTO.getUsuarioId());
        assertNull(responseDTO.getLojaId());
        assertTrue(responseDTO.getItens().isEmpty());
    }

    @Test
    void toEntity_ShouldMapCorrectly() {
        // Given
        PedidoRequestDTO requestDTO = new PedidoRequestDTO();
        requestDTO.setUsuarioId(10L);
        requestDTO.setLojaId(20L);
        requestDTO.setItens(List.of(new ItemPedidoSimplesDTO(2, 1L)));

        // When
        Pedido pedido = mapper.toEntity(requestDTO);

        // Then
        assertNotNull(pedido);
        assertNull(pedido.getUsuario());
        assertNull(pedido.getLoja());
        assertTrue(pedido.getItens().isEmpty()); // itens são tratados na Facade
    }
}
