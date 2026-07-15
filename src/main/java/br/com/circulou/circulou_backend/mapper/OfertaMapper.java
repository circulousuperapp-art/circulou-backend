package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.OfertaRequestDTO;
import br.com.circulou.circulou_backend.dto.OfertaResponseDTO;
import br.com.circulou.circulou_backend.model.Oferta;
import org.springframework.stereotype.Component;

@Component
public class OfertaMapper {

    public OfertaResponseDTO toResponseDTO(Oferta oferta) {
        if (oferta == null) return null;

        return new OfertaResponseDTO(
                oferta.getId(),
                oferta.getLoja() != null ? oferta.getLoja().getId() : null,
                oferta.getProduto() != null ? oferta.getProduto().getId() : null,
                oferta.getPreco(),
                oferta.getEstoque(),
                oferta.getEstoqueMinimo(),
                oferta.getAtivo(),
                oferta.getDisponivel(),
                oferta.getPermiteRetirada(),
                oferta.getPermiteEntrega(),
                oferta.getDataCriacao(),
                oferta.getDataAtualizacao()
        );
    }

    public Oferta toEntity(OfertaRequestDTO dto) {
        if (dto == null) return null;

        Oferta oferta = new Oferta();
        updateEntityFromDto(oferta, dto);
        return oferta;
    }

    public void updateEntityFromDto(Oferta oferta, OfertaRequestDTO dto) {
        if (dto == null) return;

        oferta.setPreco(dto.getPreco());
        oferta.setEstoque(dto.getEstoque());
        oferta.setEstoqueMinimo(dto.getEstoqueMinimo());
        
        if (dto.getAtivo() != null) {
            oferta.setAtivo(dto.getAtivo());
        }
        
        if (dto.getDisponivel() != null) {
            oferta.setDisponivel(dto.getDisponivel());
        }
        
        if (dto.getPermiteRetirada() != null) {
            oferta.setPermiteRetirada(dto.getPermiteRetirada());
        }
        
        if (dto.getPermiteEntrega() != null) {
            oferta.setPermiteEntrega(dto.getPermiteEntrega());
        }
    }
}
