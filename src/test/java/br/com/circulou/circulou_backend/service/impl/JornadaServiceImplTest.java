package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.Jornada;
import br.com.circulou.circulou_backend.model.JornadaParadaStatus;
import br.com.circulou.circulou_backend.model.JornadaStatus;
import br.com.circulou.circulou_backend.model.JornadaTipo;
import br.com.circulou.circulou_backend.port.in.command.CriarJornadaCommand;
import br.com.circulou.circulou_backend.port.out.JornadaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaServiceImplTest {

    @Mock
    private JornadaRepositoryPort repository;

    @InjectMocks
    private JornadaServiceImpl service;

    private Long jornadaId = 1L;
    private Long clienteId = 10L;

    @Test
    @DisplayName("Deve criar jornada própria com paradas e persistir")
    void deveCriarJornadaPropriaComParadas() {
        // Arrange
        var paradas = List.of(new CriarJornadaCommand.Parada(1L, 100L, 1));
        var command = new CriarJornadaCommand(clienteId, JornadaTipo.PROPRIA, paradas);

        when(repository.save(any(Jornada.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Jornada result = service.criar(command);

        // Assert
        assertNotNull(result);
        assertEquals(clienteId, result.getClienteId());
        assertEquals(JornadaTipo.PROPRIA, result.getTipo());
        assertEquals(1, result.getParadas().size());
        verify(repository).save(any(Jornada.class));
    }

    @Test
    @DisplayName("Deve criar jornada motorista inicialmente sem corridaId")
    void deveCriarJornadaMotoristaSemCorrida() {
        // Arrange
        var command = new CriarJornadaCommand(clienteId, JornadaTipo.MOTORISTA, List.of());
        when(repository.save(any(Jornada.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Jornada result = service.criar(command);

        // Assert
        assertEquals(JornadaTipo.MOTORISTA, result.getTipo());
        assertNull(result.getCorridaId());
    }

    @Test
    @DisplayName("Deve buscar jornada existente por ID")
    void deveBuscarJornadaPorId() {
        // Arrange
        Jornada jornada = new Jornada(clienteId, JornadaTipo.PROPRIA);
        when(repository.findById(jornadaId)).thenReturn(Optional.of(jornada));

        // Act
        Jornada result = service.buscarPorId(jornadaId);

        // Assert
        assertEquals(jornada, result);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException para jornada inexistente")
    void deveLancarExceptionJornadaInexistente() {
        // Arrange
        when(repository.findById(jornadaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(jornadaId));
    }

    @Test
    @DisplayName("iniciar: findById -> domínio -> save")
    void deveIniciarJornada() {
        // Arrange
        Jornada jornada = new Jornada(clienteId, JornadaTipo.PROPRIA);
        jornada.adicionarParada(1L, 1L, 1);
        when(repository.findById(jornadaId)).thenReturn(Optional.of(jornada));

        // Act
        service.iniciar(jornadaId);

        // Assert
        assertEquals(JornadaStatus.EM_EXECUCAO, jornada.getStatus());
        verify(repository).save(jornada);
    }

    @Test
    @DisplayName("iniciarDeslocamentoProximaParada: findById -> domínio -> save")
    void deveIniciarDeslocamento() {
        // Arrange
        Jornada jornada = new Jornada(clienteId, JornadaTipo.PROPRIA);
        jornada.adicionarParada(1L, 1L, 1);
        jornada.iniciar();
        when(repository.findById(jornadaId)).thenReturn(Optional.of(jornada));

        // Act
        service.iniciarDeslocamentoProximaParada(jornadaId);

        // Assert
        assertEquals(JornadaParadaStatus.EM_TRANSITO, jornada.getParadas().get(0).getStatus());
        verify(repository).save(jornada);
    }

    @Test
    @DisplayName("registrarChegadaNaLoja: findById -> domínio -> save")
    void deveRegistrarChegada() {
        // Arrange
        Jornada jornada = new Jornada(clienteId, JornadaTipo.PROPRIA);
        jornada.adicionarParada(1L, 1L, 1);
        jornada.iniciar();
        jornada.iniciarDeslocamentoProximaParada();
        when(repository.findById(jornadaId)).thenReturn(Optional.of(jornada));

        // Act
        service.registrarChegadaNaLoja(jornadaId);

        // Assert
        assertEquals(JornadaParadaStatus.NA_LOJA, jornada.getParadas().get(0).getStatus());
        verify(repository).save(jornada);
    }

    @Test
    @DisplayName("confirmarRetirada: findById -> domínio -> save")
    void deveConfirmarRetirada() {
        // Arrange
        Jornada jornada = new Jornada(clienteId, JornadaTipo.PROPRIA);
        jornada.adicionarParada(1L, 1L, 1);
        jornada.iniciar();
        jornada.iniciarDeslocamentoProximaParada();
        jornada.registrarChegadaNaLoja();
        when(repository.findById(jornadaId)).thenReturn(Optional.of(jornada));

        // Act
        service.confirmarRetirada(jornadaId);

        // Assert
        assertEquals(JornadaParadaStatus.RETIRADA_REALIZADA, jornada.getParadas().get(0).getStatus());
        verify(repository).save(jornada);
    }

    @Test
    @DisplayName("cancelarParada: findById -> domínio -> save")
    void deveCancelarParada() {
        // Arrange
        Jornada jornada = new Jornada(clienteId, JornadaTipo.PROPRIA);
        jornada.adicionarParada(1L, 1L, 1);
        when(repository.findById(jornadaId)).thenReturn(Optional.of(jornada));

        // Act
        service.cancelarParada(jornadaId, 1);

        // Assert
        assertEquals(JornadaParadaStatus.CANCELADA, jornada.getParadas().get(0).getStatus());
        verify(repository).save(jornada);
    }

    @Test
    @DisplayName("concluir: findById -> domínio -> save")
    void deveConcluirJornada() {
        // Arrange
        Jornada jornada = new Jornada(clienteId, JornadaTipo.PROPRIA);
        jornada.adicionarParada(1L, 1L, 1);
        jornada.iniciar();
        jornada.iniciarDeslocamentoProximaParada();
        jornada.registrarChegadaNaLoja();
        jornada.confirmarRetirada();
        when(repository.findById(jornadaId)).thenReturn(Optional.of(jornada));

        // Act
        service.concluir(jornadaId);

        // Assert
        assertEquals(JornadaStatus.CONCLUIDA, jornada.getStatus());
        verify(repository).save(jornada);
    }

    @Test
    @DisplayName("cancelar: findById -> domínio -> save")
    void deveCancelarJornada() {
        // Arrange
        Jornada jornada = new Jornada(clienteId, JornadaTipo.PROPRIA);
        when(repository.findById(jornadaId)).thenReturn(Optional.of(jornada));

        // Act
        service.cancelar(jornadaId);

        // Assert
        assertEquals(JornadaStatus.CANCELADA, jornada.getStatus());
        verify(repository).save(jornada);
    }
}
