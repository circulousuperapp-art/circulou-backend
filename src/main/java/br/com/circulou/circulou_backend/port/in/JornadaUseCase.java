package br.com.circulou.circulou_backend.port.in;

import br.com.circulou.circulou_backend.model.Jornada;
import br.com.circulou.circulou_backend.port.in.command.CriarJornadaCommand;

public interface JornadaUseCase {

    Jornada criar(CriarJornadaCommand command);

    Jornada buscarPorId(Long id);

    void iniciar(Long id);

    void iniciarDeslocamentoProximaParada(Long id);

    void registrarChegadaNaLoja(Long id);

    void confirmarRetirada(Long id);

    void cancelarParada(Long id, Integer ordem);

    void concluir(Long id);

    void cancelar(Long id);
}
