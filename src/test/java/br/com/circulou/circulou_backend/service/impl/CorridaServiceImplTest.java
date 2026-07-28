package br.com.circulou.circulou_backend.service.impl;

import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import br.com.circulou.circulou_backend.model.Corrida;
import br.com.circulou.circulou_backend.model.CorridaStatus;
import br.com.circulou.circulou_backend.model.CorridaStatusPolicy;
import br.com.circulou.circulou_backend.port.out.CorridaRepositoryPort;
import br.com.circulou.circulou_backend.port.out.EventPublisherPort;
import br.com.circulou.circulou_backend.port.out.MetricsPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorridaServiceImplTest {

    @Mock
    private CorridaRepositoryPort repositoryPort;

    @Mock
    private EventPublisherPort eventPublisher;

    @Mock
    private CorridaStatusPolicy statusPolicy;

    @Mock
    private MetricsPort metricsPort;

    @InjectMocks
    private CorridaServiceImpl service;

    @Test
    @DisplayName("Deve salvar uma nova corrida com status SOLICITADA")
    void deveSalvarNovaCorrida() {
        Corrida corrida = new Corrida();
        when(repositoryPort.save(any(Corrida.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Corrida salva = service.salvar(corrida);

        assertThat(salva.getStatus()).isEqualTo(CorridaStatus.SOLICITADA);
        assertThat(salva.getDataCriacao()).isNotNull();
        verify(repositoryPort).save(corrida);
        verify(metricsPort).incrementarCorridasCriadas();
    }

    @Test
    @DisplayName("Deve buscar corrida por ID")
    void deveBuscarPorId() {
        Corrida corrida = new Corrida();
        corrida.setId(1L);
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(corrida));

        Corrida encontrada = service.buscarPorId(1L);

        assertThat(encontrada).isEqualTo(corrida);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar corrida inexistente")
    void deveLancarExcecaoAoBuscarInexistente() {
        when(repositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Corrida não encontrada");
    }

    @Test
    @DisplayName("Deve alterar status da corrida")
    void deveAlterarStatus() {
        Corrida corrida = new Corrida();
        corrida.setId(1L);
        corrida.setStatus(CorridaStatus.SOLICITADA);
        
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(corrida));
        when(statusPolicy.isTransicaoValida(CorridaStatus.SOLICITADA, CorridaStatus.ACEITA)).thenReturn(true);
        when(repositoryPort.save(any(Corrida.class))).thenReturn(corrida);

        service.alterarStatus(1L, CorridaStatus.ACEITA);

        assertThat(corrida.getStatus()).isEqualTo(CorridaStatus.ACEITA);
        verify(repositoryPort).save(corrida);
        verify(eventPublisher).publish(anyList());
    }

    @Test
    @DisplayName("Deve deletar corrida existente")
    void deveDeletarCorrida() {
        when(repositoryPort.existsById(1L)).thenReturn(true);

        service.deletar(1L);

        verify(repositoryPort).deleteById(1L);
    }
}
