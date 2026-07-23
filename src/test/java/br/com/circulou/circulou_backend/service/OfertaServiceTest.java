package br.com.circulou.circulou_backend.service;

import br.com.circulou.circulou_backend.exception.BusinessException;
import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.model.Oferta;
import br.com.circulou.circulou_backend.model.Produto;
import br.com.circulou.circulou_backend.port.out.OfertaRepositoryPort;
import br.com.circulou.circulou_backend.service.impl.OfertaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfertaServiceTest {

    @Mock
    private OfertaRepositoryPort repositoryPort;

    @InjectMocks
    private OfertaServiceImpl ofertaService;

    private Oferta oferta;
    private Loja loja;
    private Produto produto;

    @BeforeEach
    void setUp() {
        loja = new Loja();
        loja.setId(1L);
        loja.setAtiva(true);

        produto = new Produto();
        produto.setId(1L);
        produto.setAtivo(true);
        produto.setNome("Produto Teste");

        oferta = new Oferta();
        oferta.setId(1L);
        oferta.setLoja(loja);
        oferta.setProduto(produto);
        oferta.setPreco(new BigDecimal("10.00"));
        oferta.setEstoque(10);
        oferta.setAtivo(true);
        oferta.setDisponivel(true);
    }

    @Test
    @DisplayName("Deve listar todas as ofertas")
    void deveListarTodasOfertas() {
        when(repositoryPort.findAll()).thenReturn(List.of(oferta));

        List<Oferta> resultado = ofertaService.listarTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repositoryPort).findAll();
    }

    @Test
    @DisplayName("Deve listar ofertas com paginação")
    void deveListarOfertasComPaginacao() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Oferta> page = new PageImpl<>(List.of(oferta));
        when(repositoryPort.findAll(pageable)).thenReturn(page);

        Page<Oferta> resultado = ofertaService.listarTodas(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(repositoryPort).findAll(pageable);
    }

    @Test
    @DisplayName("Deve validar oferta para venda com sucesso")
    void deveValidarParaVendaComSucesso() {
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(oferta));

        assertDoesNotThrow(() -> ofertaService.validarParaVenda(1L, 1L, 5));
        
        verify(repositoryPort).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando oferta não existe")
    void deveLancarExcecaoQuandoOfertaNaoExiste() {
        when(repositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ofertaService.validarParaVenda(1L, 1L, 5));
    }

    @Test
    @DisplayName("Deve lançar exceção quando oferta está inativa")
    void deveLancarExcecaoQuandoOfertaInativa() {
        oferta.setAtivo(false);
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(oferta));

        assertThrows(BusinessException.class, () -> ofertaService.validarParaVenda(1L, 1L, 5));
    }

    @Test
    @DisplayName("Deve lançar exceção quando oferta não está disponível")
    void deveLancarExcecaoQuandoOfertaIndisponivel() {
        oferta.setDisponivel(false);
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(oferta));

        assertThrows(BusinessException.class, () -> ofertaService.validarParaVenda(1L, 1L, 5));
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto está inativo")
    void deveLancarExcecaoQuandoProdutoInativo() {
        produto.setAtivo(false);
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(oferta));

        assertThrows(BusinessException.class, () -> ofertaService.validarParaVenda(1L, 1L, 5));
    }

    @Test
    @DisplayName("Deve lançar exceção quando oferta pertence a outra loja")
    void deveLancarExcecaoQuandoLojaDiferente() {
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(oferta));

        assertThrows(BusinessException.class, () -> ofertaService.validarParaVenda(1L, 2L, 5));
    }

    @Test
    @DisplayName("Deve lançar exceção quando estoque é insuficiente")
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(oferta));

        assertThrows(BusinessException.class, () -> ofertaService.validarParaVenda(1L, 1L, 11));
    }

    @Test
    @DisplayName("Deve registrar venda e atualizar disponibilidade se estoque zerar")
    void deveRegistrarVendaEZerarEstoque() {
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(oferta));

        ofertaService.registrarVenda(1L, 10);

        assertEquals(0, oferta.getEstoque());
        assertFalse(oferta.getDisponivel());
        verify(repositoryPort).save(oferta);
    }
}
