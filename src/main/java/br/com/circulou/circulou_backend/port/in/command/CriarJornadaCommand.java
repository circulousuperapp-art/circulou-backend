package br.com.circulou.circulou_backend.port.in.command;

import br.com.circulou.circulou_backend.model.JornadaTipo;
import java.util.List;

public record CriarJornadaCommand(
    Long clienteId,
    JornadaTipo tipo,
    List<Parada> paradas
) {
    public record Parada(
        Long lojaId,
        Long pedidoId,
        Integer ordem
    ) {}
}
