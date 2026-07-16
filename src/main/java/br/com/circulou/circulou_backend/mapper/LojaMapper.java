package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.LojaRequestDTO;
import br.com.circulou.circulou_backend.dto.LojaResponseDTO;
import br.com.circulou.circulou_backend.model.Loja;
import org.springframework.stereotype.Component;

@Component
public class LojaMapper {

    public LojaResponseDTO toResponseDTO(Loja loja) {
        return new LojaResponseDTO(
                loja.getId(),
                loja.getNome(),
                loja.getEmail(),
                loja.getTelefone(),
                loja.getLogo(),
                loja.getTempoMedioPreparo(),
                loja.getAtiva(),
                loja.getSeloConfianca(),
                loja.getLojistaProfile() != null ? loja.getLojistaProfile().getId() : null
        );
    }

    public Loja toEntity(LojaRequestDTO dto) {
        Loja loja = new Loja();
        updateEntityFromDto(loja, dto);
        return loja;
    }

    public void updateEntityFromDto(Loja loja, LojaRequestDTO dto) {

        loja.setNome(dto.getNome());
        loja.setEmail(dto.getEmail());
        loja.setTelefone(dto.getTelefone());
        loja.setLogo(dto.getLogo());
        loja.setTempoMedioPreparo(dto.getTempoMedioPreparo());

        if (dto.getAtiva() != null) {
            loja.setAtiva(dto.getAtiva());
        } else {
            loja.setAtiva(true);
        }

        loja.setSeloConfianca(dto.getSeloConfianca());
        loja.setEndereco(dto.getEndereco());
    }
}