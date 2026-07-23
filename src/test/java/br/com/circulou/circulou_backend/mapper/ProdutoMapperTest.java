package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.ProdutoRequestDTO;
import br.com.circulou.circulou_backend.dto.ProdutoResponseDTO;
import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoMapperTest {

    private ProdutoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProdutoMapper();
    }

    @Test
    void toResponseDTO_ShouldMapCorrectly() {
        // Given
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Hambúrguer");
        produto.setDescricao("Delicioso");
        produto.setMarca("Marca Famosa");
        produto.setUnidadeMedida("kg");
        produto.setPeso(new BigDecimal("1.500"));
        produto.setCodigoBarras("7891234567890");
        produto.setImagemPrincipal("http://img.com");
        produto.setAtivo(true);

        // When
        ProdutoResponseDTO responseDTO = mapper.toResponseDTO(produto);

        // Then
        assertNotNull(responseDTO);
        assertEquals(produto.getId(), responseDTO.getId());
        assertEquals(produto.getNome(), responseDTO.getNome());
        assertEquals(produto.getDescricao(), responseDTO.getDescricao());
        assertEquals(produto.getMarca(), responseDTO.getMarca());
        assertEquals(produto.getUnidadeMedida(), responseDTO.getUnidadeMedida());
        assertEquals(produto.getPeso(), responseDTO.getPeso());
        assertEquals(produto.getCodigoBarras(), responseDTO.getCodigoBarras());
        assertEquals(produto.getImagemPrincipal(), responseDTO.getImagemPrincipal());
        assertEquals(produto.getAtivo(), responseDTO.getAtivo());
    }

    @Test
    void toEntity_ShouldMapCorrectly() {
        // Given
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Pizza");
        requestDTO.setDescricao("Calabresa");
        requestDTO.setMarca("Forno");
        requestDTO.setUnidadeMedida("un");
        requestDTO.setPeso(new BigDecimal("0.800"));
        requestDTO.setCodigoBarras("7890000000001");
        requestDTO.setImagemPrincipal("http://pizza.com");
        requestDTO.setAtivo(true);

        // When
        Produto produto = mapper.toEntity(requestDTO);

        // Then
        assertNotNull(produto);
        assertEquals(requestDTO.getNome(), produto.getNome());
        assertEquals(requestDTO.getDescricao(), produto.getDescricao());
        assertEquals(requestDTO.getMarca(), produto.getMarca());
        assertEquals(requestDTO.getUnidadeMedida(), produto.getUnidadeMedida());
        assertEquals(requestDTO.getPeso(), produto.getPeso());
        assertEquals(requestDTO.getCodigoBarras(), produto.getCodigoBarras());
        assertEquals(requestDTO.getImagemPrincipal(), produto.getImagemPrincipal());
        assertEquals(requestDTO.getAtivo(), produto.getAtivo());
    }

    @Test
    void updateEntityFromDto_ShouldUpdateFields() {
        // Given
        Produto produto = new Produto();
        produto.setNome("Antigo");

        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Novo");
        requestDTO.setDescricao("Nova Desc");

        // When
        mapper.updateEntityFromDto(produto, requestDTO);

        // Then
        assertEquals(requestDTO.getNome(), produto.getNome());
        assertEquals(requestDTO.getDescricao(), produto.getDescricao());
    }
}
