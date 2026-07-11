package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.ProdutoRequestDTO;
import br.com.circulou.circulou_backend.dto.ProdutoResponseDTO;
import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Loja loja = new Loja();
        loja.setId(10L);
        loja.setNome("Loja Teste");

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Hambúrguer");
        produto.setDescricao("Delicioso");
        produto.setPreco(35.0);
        produto.setEstoque(10);
        produto.setCategoria("Lanches");
        produto.setImagem("http://img.com");
        produto.setAtivo(true);
        produto.setLoja(loja);

        // When
        ProdutoResponseDTO responseDTO = mapper.toResponseDTO(produto);

        // Then
        assertNotNull(responseDTO);
        assertEquals(produto.getId(), responseDTO.getId());
        assertEquals(produto.getNome(), responseDTO.getNome());
        assertEquals(produto.getDescricao(), responseDTO.getDescricao());
        assertEquals(produto.getPreco(), responseDTO.getPreco());
        assertEquals(produto.getEstoque(), responseDTO.getEstoque());
        assertEquals(produto.getCategoria(), responseDTO.getCategoria());
        assertEquals(produto.getImagem(), responseDTO.getImagem());
        assertEquals(produto.getAtivo(), responseDTO.getAtivo());
        assertEquals(loja, responseDTO.getLoja());
    }

    @Test
    void toEntity_ShouldMapCorrectly() {
        // Given
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Pizza");
        requestDTO.setDescricao("Calabresa");
        requestDTO.setPreco(45.0);
        requestDTO.setEstoque(5);
        requestDTO.setCategoria("Pizzas");
        requestDTO.setImagem("http://pizza.com");
        requestDTO.setAtivo(true);
        requestDTO.setLojaId(10L);

        // When
        Produto produto = mapper.toEntity(requestDTO);

        // Then
        assertNotNull(produto);
        assertEquals(requestDTO.getNome(), produto.getNome());
        assertEquals(requestDTO.getDescricao(), produto.getDescricao());
        assertEquals(requestDTO.getPreco(), produto.getPreco());
        assertEquals(requestDTO.getEstoque(), produto.getEstoque());
        assertEquals(requestDTO.getCategoria(), produto.getCategoria());
        assertEquals(requestDTO.getImagem(), produto.getImagem());
        assertEquals(requestDTO.getAtivo(), produto.getAtivo());
        assertNull(produto.getLoja());
    }

    @Test
    void updateEntityFromDto_ShouldUpdateFields() {
        // Given
        Produto produto = new Produto();
        produto.setNome("Antigo");

        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Novo");
        requestDTO.setPreco(20.0);

        // When
        mapper.updateEntityFromDto(produto, requestDTO);

        // Then
        assertEquals(requestDTO.getNome(), produto.getNome());
        assertEquals(requestDTO.getPreco(), produto.getPreco());
    }
}
