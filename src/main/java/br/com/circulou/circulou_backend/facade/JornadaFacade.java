package br.com.circulou.circulou_backend.facade;

import br.com.circulou.circulou_backend.dto.JornadaResponseDTO;

public interface JornadaFacade {
    JornadaResponseDTO buscarMeuPorId(Long id);
    void iniciar(Long id);
    void iniciarDeslocamentoProximaParada(Long id);
    void registrarChegadaNaLoja(Long id);
    void confirmarRetirada(Long id);
    void cancelarParada(Long id, Integer ordem);
    void concluir(Long id);
    void cancelar(Long id);
}
