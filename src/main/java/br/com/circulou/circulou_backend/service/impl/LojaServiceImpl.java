package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.Loja;
import br.com.circulou.circulou_backend.port.out.LojaRepositoryPort;
import br.com.circulou.circulou_backend.service.LojaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LojaServiceImpl implements LojaService {

    private final LojaRepositoryPort lojaRepositoryPort;

    public LojaServiceImpl(LojaRepositoryPort lojaRepositoryPort) {
        this.lojaRepositoryPort = lojaRepositoryPort;
    }

    @Override
    public List<Loja> listarTodos() {
        return lojaRepositoryPort.findAll();
    }

    @Override
    public Loja buscarPorId(Long id) {
        return buscarEntidadePorId(id);
    }

    @Override
    @Transactional
    public Loja salvar(Loja loja) {
        return lojaRepositoryPort.save(loja);
    }

    @Override
    @Transactional
    public Loja atualizar(Long id, Loja loja) {
        buscarEntidadePorId(id);
        loja.setId(id);
        return lojaRepositoryPort.save(loja);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        Loja loja = buscarEntidadePorId(id);
        loja.setAtiva(false);
        lojaRepositoryPort.save(loja);
    }

    private Loja buscarEntidadePorId(Long id) {
        return lojaRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loja não encontrada"));
    }
}
