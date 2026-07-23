package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.dto.OfertaRequestDTO;
import br.com.circulou.circulou_backend.dto.OfertaResponseDTO;
import br.com.circulou.circulou_backend.mapper.OfertaMapper;
import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.model.Oferta;
import br.com.circulou.circulou_backend.model.Produto;
import br.com.circulou.circulou_backend.port.in.OfertaUseCase;
import br.com.circulou.circulou_backend.service.LojaService;
import br.com.circulou.circulou_backend.service.OfertaService;
import br.com.circulou.circulou_backend.service.ProdutoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class OfertaFacadeImpl implements OfertaUseCase {

    private final OfertaService ofertaService;
    private final LojaService lojaService;
    private final ProdutoService produtoService;
    private final OfertaMapper ofertaMapper;

    public OfertaFacadeImpl(OfertaService ofertaService,
                            LojaService lojaService,
                            ProdutoService produtoService,
                            OfertaMapper ofertaMapper) {
        this.ofertaService = ofertaService;
        this.lojaService = lojaService;
        this.produtoService = produtoService;
        this.ofertaMapper = ofertaMapper;
    }

    @Override
    public List<OfertaResponseDTO> listarTodas() {
        return ofertaService.listarTodas()
                .stream()
                .map(ofertaMapper::toResponseDTO)
                .toList();
    }

    @Override
    public Page<OfertaResponseDTO> listarTodas(Pageable pageable) {
        return ofertaService.listarTodas(pageable)
                .map(ofertaMapper::toResponseDTO);
    }

    @Override
    public OfertaResponseDTO buscarPorId(Long id) {
        Oferta oferta = ofertaService.buscarPorId(id);
        return ofertaMapper.toResponseDTO(oferta);
    }

    @Override
    public List<OfertaResponseDTO> listarPorLoja(Long lojaId) {
        return ofertaService.listarPorLoja(lojaId)
                .stream()
                .map(ofertaMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<OfertaResponseDTO> listarPorProduto(Long produtoId) {
        return ofertaService.listarPorProduto(produtoId)
                .stream()
                .map(ofertaMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public OfertaResponseDTO salvar(OfertaRequestDTO dto) {
        Oferta oferta = ofertaMapper.toEntity(dto);
        vincularRelacionamentos(oferta, dto.getLojaId(), dto.getProdutoId());
        
        Oferta ofertaSalva = ofertaService.salvar(oferta);
        return ofertaMapper.toResponseDTO(ofertaSalva);
    }

    @Override
    @Transactional
    public OfertaResponseDTO atualizar(Long id, OfertaRequestDTO dto) {
        Oferta oferta = ofertaService.buscarPorId(id);
        
        ofertaMapper.updateEntityFromDto(oferta, dto);
        vincularRelacionamentos(oferta, dto.getLojaId(), dto.getProdutoId());
        
        Oferta ofertaAtualizada = ofertaService.atualizar(id, oferta);
        return ofertaMapper.toResponseDTO(ofertaAtualizada);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        ofertaService.deletar(id);
    }

    private void vincularRelacionamentos(Oferta oferta, Long lojaId, Long produtoId) {
        if (lojaId != null) {
            Loja loja = lojaService.buscarPorId(lojaId);
            oferta.setLoja(loja);
        }
        if (produtoId != null) {
            Produto produto = produtoService.buscarPorId(produtoId);
            oferta.setProduto(produto);
        }
    }
}
