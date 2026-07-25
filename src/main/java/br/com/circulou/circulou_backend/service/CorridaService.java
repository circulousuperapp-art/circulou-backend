package br.com.circulou.circulou_backend.service;

import br.com.circulou.circulou_backend.model.Corrida;
import br.com.circulou.circulou_backend.model.CorridaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CorridaService {
    List<Corrida> listarTodas();
    Page<Corrida> listarTodas(Pageable pageable);
    Corrida buscarPorId(Long id);
    List<Corrida> listarPorCliente(Long clienteId);
    List<Corrida> listarPorMotorista(Long motoristaId);
    Corrida salvar(Corrida corrida);
    Corrida atualizar(Long id, Corrida corrida);
    void deletar(Long id);
    void alterarStatus(Long id, CorridaStatus novoStatus);
}
