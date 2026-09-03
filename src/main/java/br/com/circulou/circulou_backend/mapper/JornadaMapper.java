package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.JornadaParadaResponseDTO;
import br.com.circulou.circulou_backend.dto.JornadaResponseDTO;
import br.com.circulou.circulou_backend.model.Jornada;
import br.com.circulou.circulou_backend.model.JornadaParada;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class JornadaMapper {

    public JornadaResponseDTO toResponseDTO(Jornada jornada) {
        if (jornada == null) return null;

        List<JornadaParadaResponseDTO> paradas = jornada.getParadas() != null ?
                jornada.getParadas().stream()
                        .map(this::toParadaResponseDTO)
                        .toList() :
                Collections.emptyList();

        return new JornadaResponseDTO(
                jornada.getId(),
                jornada.getClienteId(),
                jornada.getCorridaId(),
                jornada.getTipo(),
                jornada.getStatus(),
                jornada.getCreatedAt(),
                jornada.getUpdatedAt(),
                paradas
        );
    }

    public JornadaParadaResponseDTO toParadaResponseDTO(JornadaParada parada) {
        if (parada == null) return null;

        return new JornadaParadaResponseDTO(
                parada.getLojaId(),
                parada.getPedidoId(),
                parada.getOrdem(),
                parada.getStatus()
        );
    }
}
