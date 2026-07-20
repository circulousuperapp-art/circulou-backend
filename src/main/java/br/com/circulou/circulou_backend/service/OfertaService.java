package br.com.circulou.circulou_backend.service;

import br.com.circulou.circulou_backend.model.Oferta;
import java.util.List;

public interface OfertaService {
    List<Oferta> listarTodas();
    Oferta buscarPorId(Long id);
    List<Oferta> listarPorLoja(Long lojaId);
    List<Oferta> listarPorProduto(Long produtoId);
    Oferta salvar(Oferta oferta);
    Oferta atualizar(Long id, Oferta oferta);
    void deletar(Long id);
    void registrarVenda(Long ofertaId, Integer quantidade);
    void validarParaVenda(Long ofertaId, Long lojaId, Integer quantidade);
}
