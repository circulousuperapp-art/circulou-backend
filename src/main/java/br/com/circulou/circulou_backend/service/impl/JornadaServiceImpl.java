package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.Jornada;
import br.com.circulou.circulou_backend.port.in.JornadaUseCase;
import br.com.circulou.circulou_backend.port.in.command.CriarJornadaCommand;
import br.com.circulou.circulou_backend.port.out.JornadaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class JornadaServiceImpl implements JornadaUseCase {

    private final JornadaRepositoryPort jornadaRepository;

    public JornadaServiceImpl(JornadaRepositoryPort jornadaRepository) {
        this.jornadaRepository = jornadaRepository;
    }

    @Override
    @Transactional
    public Jornada criar(CriarJornadaCommand command) {
        Jornada jornada = new Jornada(command.clienteId(), command.tipo());

        if (command.paradas() != null) {
            command.paradas().forEach(p ->
                jornada.adicionarParada(p.lojaId(), p.pedidoId(), p.ordem())
            );
        }

        return jornadaRepository.save(jornada);
    }

    @Override
    public Jornada buscarPorId(Long id) {
        return jornadaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jornada não encontrada"));
    }

    @Override
    @Transactional
    public void iniciar(Long id) {
        Jornada jornada = buscarPorId(id);
        jornada.iniciar();
        jornadaRepository.save(jornada);
    }

    @Override
    @Transactional
    public void iniciarDeslocamentoProximaParada(Long id) {
        Jornada jornada = buscarPorId(id);
        jornada.iniciarDeslocamentoProximaParada();
        jornadaRepository.save(jornada);
    }

    @Override
    @Transactional
    public void registrarChegadaNaLoja(Long id) {
        Jornada jornada = buscarPorId(id);
        jornada.registrarChegadaNaLoja();
        jornadaRepository.save(jornada);
    }

    @Override
    @Transactional
    public void confirmarRetirada(Long id) {
        Jornada jornada = buscarPorId(id);
        jornada.confirmarRetirada();
        jornadaRepository.save(jornada);
    }

    @Override
    @Transactional
    public void cancelarParada(Long id, Integer ordem) {
        Jornada jornada = buscarPorId(id);
        jornada.cancelarParada(ordem);
        jornadaRepository.save(jornada);
    }

    @Override
    @Transactional
    public void concluir(Long id) {
        Jornada jornada = buscarPorId(id);
        jornada.concluir();
        jornadaRepository.save(jornada);
    }

    @Override
    @Transactional
    public void cancelar(Long id) {
        Jornada jornada = buscarPorId(id);
        jornada.cancelar();
        jornadaRepository.save(jornada);
    }
}
