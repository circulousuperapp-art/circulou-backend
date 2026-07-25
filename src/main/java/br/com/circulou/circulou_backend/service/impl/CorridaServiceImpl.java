package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.Corrida;
import br.com.circulou.circulou_backend.model.CorridaStatus;
import br.com.circulou.circulou_backend.model.CorridaStatusPolicy;
import br.com.circulou.circulou_backend.port.out.CorridaRepositoryPort;
import br.com.circulou.circulou_backend.port.out.EventPublisherPort;
import br.com.circulou.circulou_backend.service.CorridaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CorridaServiceImpl implements CorridaService {

    private final CorridaRepositoryPort repositoryPort;
    private final EventPublisherPort eventPublisher;
    private final CorridaStatusPolicy statusPolicy;

    public CorridaServiceImpl(CorridaRepositoryPort repositoryPort, 
                              EventPublisherPort eventPublisher,
                              CorridaStatusPolicy statusPolicy) {
        this.repositoryPort = repositoryPort;
        this.eventPublisher = eventPublisher;
        this.statusPolicy = statusPolicy;
    }

    @Override
    public List<Corrida> listarTodas() {
        return repositoryPort.findAll();
    }

    @Override
    public Page<Corrida> listarTodas(Pageable pageable) {
        return repositoryPort.findAll(pageable);
    }

    @Override
    public Corrida buscarPorId(Long id) {
        return repositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corrida não encontrada"));
    }

    @Override
    public List<Corrida> listarPorCliente(Long clienteId) {
        return repositoryPort.findByClienteId(clienteId);
    }

    @Override
    public List<Corrida> listarPorMotorista(Long motoristaId) {
        return repositoryPort.findByMotoristaId(motoristaId);
    }

    @Override
    @Transactional
    public Corrida salvar(Corrida corrida) {
        corrida.setDataCriacao(LocalDateTime.now());
        corrida.setStatus(CorridaStatus.SOLICITADA);
        return repositoryPort.save(corrida);
    }

    @Override
    @Transactional
    public Corrida atualizar(Long id, Corrida corrida) {
        buscarPorId(id);
        corrida.setId(id);
        return repositoryPort.save(corrida);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!repositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Corrida não encontrada");
        }
        repositoryPort.deleteById(id);
    }

    @Override
    @Transactional
    public void alterarStatus(Long id, CorridaStatus novoStatus) {
        Corrida corrida = buscarPorId(id);
        corrida.transitarPara(novoStatus, statusPolicy);
        repositoryPort.save(corrida);
        eventPublisher.publish(corrida.pullDomainEvents());
    }
}
