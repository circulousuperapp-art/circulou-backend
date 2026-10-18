package br.com.circulou.circulou_backend.mapper;

import br.com.circulou.circulou_backend.dto.CorridaRequestDTO;
import br.com.circulou.circulou_backend.dto.CorridaResponseDTO;
import br.com.circulou.circulou_backend.model.Corrida;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class CorridaMapper {

    public CorridaResponseDTO toResponseDTO(Corrida corrida) {
        if (corrida == null) return null;

        String origemFormatada = formatarLocal(
                corrida.getOrigemLogradouro(),
                corrida.getOrigemNumero(),
                corrida.getOrigemBairro(),
                corrida.getOrigemCidade(),
                corrida.getOrigemEstado()
        );

        String destinoFormatado = formatarLocal(
                corrida.getDestinoLogradouro(),
                corrida.getDestinoNumero(),
                corrida.getDestinoBairro(),
                corrida.getDestinoCidade(),
                corrida.getDestinoEstado()
        );

        return new CorridaResponseDTO(
                corrida.getId(),
                corrida.getCliente() != null ? corrida.getCliente().getId() : null,
                corrida.getMotorista() != null ? corrida.getMotorista().getId() : null,
                corrida.getStatus(),
                corrida.getDataCriacao(),
                corrida.getDataInicio(),
                corrida.getDataFinalizacao(),
                corrida.getValorEstimado(),
                corrida.getValorFinal(),
                origemFormatada,
                destinoFormatado,
                corrida.getDistanciaEstimadaMetros(),
                corrida.getDuracaoEstimadaSegundos(),
                formatarDistancia(corrida.getDistanciaEstimadaMetros()),
                formatarDuracao(corrida.getDuracaoEstimadaSegundos())
        );
    }

    public Corrida toEntity(CorridaRequestDTO dto) {
        if (dto == null) return null;
        Corrida corrida = new Corrida();
        updateEntityFromDto(corrida, dto);
        return corrida;
    }

    public void updateEntityFromDto(Corrida corrida, CorridaRequestDTO dto) {
        if (dto == null) return;
        corrida.setOrigemLogradouro(dto.getOrigemLogradouro());
        corrida.setOrigemNumero(dto.getOrigemNumero());
        corrida.setOrigemBairro(dto.getOrigemBairro());
        corrida.setOrigemCidade(dto.getOrigemCidade());
        corrida.setOrigemEstado(dto.getOrigemEstado());
        corrida.setOrigemLatitude(dto.getOrigemLatitude());
        corrida.setOrigemLongitude(dto.getOrigemLongitude());

        corrida.setDestinoLogradouro(dto.getDestinoLogradouro());
        corrida.setDestinoNumero(dto.getDestinoNumero());
        corrida.setDestinoBairro(dto.getDestinoBairro());
        corrida.setDestinoCidade(dto.getDestinoCidade());
        corrida.setDestinoEstado(dto.getDestinoEstado());
        corrida.setDestinoLatitude(dto.getDestinoLatitude());
        corrida.setDestinoLongitude(dto.getDestinoLongitude());

        corrida.setValorEstimado(dto.getValorEstimado());
    }

    private String formatarLocal(String logradouro, String numero, String bairro, String cidade, String estado) {
        StringBuilder sb = new StringBuilder();
        if (logradouro != null) sb.append(logradouro);
        if (numero != null) sb.append(", ").append(numero);
        if (bairro != null) sb.append(" - ").append(bairro);
        if (cidade != null) sb.append(", ").append(cidade);
        if (estado != null) sb.append("/").append(estado);
        return sb.toString().trim();
    }

    private String formatarDistancia(Integer metros) {
        if (metros == null) return null;
        double km = metros / 1000.0;
        return String.format(Locale.of("pt", "BR"), "%.1f km", km);
    }

    private String formatarDuracao(Integer segundos) {
        if (segundos == null) return null;
        long minutos = Math.round(segundos / 60.0);
        return String.format("%d min", minutos);
    }
}
