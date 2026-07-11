package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.PedidoRequestDTO;
import br.com.circulou.circulou_backend.dto.PedidoResponseDTO;
import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.model.Pedido;
import br.com.circulou.circulou_backend.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PedidoMapperTest {

    private PedidoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PedidoMapper();
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
        pedido.setValorTotal(150.0);
        pedido.setStatus("PENDENTE");
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setUsuario(usuario);
        pedido.setLoja(loja);

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

        // When
        PedidoResponseDTO responseDTO = mapper.toResponseDTO(pedido);

        // Then
        assertNotNull(responseDTO);
        assertNull(responseDTO.getUsuarioId());
        assertNull(responseDTO.getLojaId());
    }

    @Test
    void toEntity_ShouldMapCorrectly() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        PedidoRequestDTO requestDTO = new PedidoRequestDTO();
        requestDTO.setValorTotal(200.0);
        requestDTO.setStatus("ENTREGUE");
        requestDTO.setDataCriacao(now);
        requestDTO.setUsuarioId(10L);
        requestDTO.setLojaId(20L);

        // When
        Pedido pedido = mapper.toEntity(requestDTO);

        // Then
        assertNotNull(pedido);
        assertEquals(requestDTO.getValorTotal(), pedido.getValorTotal());
        assertEquals(requestDTO.getStatus(), pedido.getStatus());
        assertEquals(requestDTO.getDataCriacao(), pedido.getDataCriacao());
        assertNull(pedido.getUsuario());
        assertNull(pedido.getLoja());
    }

    @Test
    void updateEntityFromDto_ShouldUpdateFields() {
        // Given
        Pedido pedido = new Pedido();
        pedido.setStatus("PENDENTE");

        LocalDateTime now = LocalDateTime.now();
        PedidoRequestDTO requestDTO = new PedidoRequestDTO();
        requestDTO.setStatus("CANCELADO");
        requestDTO.setValorTotal(0.0);
        requestDTO.setDataCriacao(now);

        // When
        mapper.updateEntityFromDto(pedido, requestDTO);

        // Then
        assertEquals(requestDTO.getStatus(), pedido.getStatus());
        assertEquals(requestDTO.getValorTotal(), pedido.getValorTotal());
        assertEquals(requestDTO.getDataCriacao(), pedido.getDataCriacao());
    }
}
