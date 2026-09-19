package br.com.circulou.circulou_backend.facade.impl;

import br.com.circulou.circulou_backend.adapter.out.maps.RouteCalculationException;
import br.com.circulou.circulou_backend.dto.CorridaRequestDTO;
import br.com.circulou.circulou_backend.dto.CorridaResponseDTO;
import br.com.circulou.circulou_backend.mapper.CorridaMapper;
import br.com.circulou.circulou_backend.model.Corrida;
import br.com.circulou.circulou_backend.model.Usuario;
import br.com.circulou.circulou_backend.port.out.GeoCoordinate;
import br.com.circulou.circulou_backend.port.out.MotoristaProfileRepositoryPort;
import br.com.circulou.circulou_backend.port.out.RouteData;
import br.com.circulou.circulou_backend.port.out.RoutePort;
import br.com.circulou.circulou_backend.port.out.UsuarioRepositoryPort;
import br.com.circulou.circulou_backend.service.CorridaService;
import org.junit.jupiter.api.BeforeEach;
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
class CorridaFacadeImplTest {

    @Mock
    private CorridaService corridaService;
    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;
    @Mock
    private MotoristaProfileRepositoryPort motoristaRepositoryPort;
    @Mock
    private CorridaMapper corridaMapper;
    @Mock
    private RoutePort routePort;

    @InjectMocks
    private CorridaFacadeImpl facade;

    private CorridaRequestDTO requestDTO;
    private Usuario cliente;
    private Corrida corrida;

    @BeforeEach
    void setUp() {
        requestDTO = new CorridaRequestDTO();
        requestDTO.setClienteId(1L);
        requestDTO.setOrigemLatitude(-23.5505);
        requestDTO.setOrigemLongitude(-46.6333);
        requestDTO.setDestinoLatitude(-23.5596);
        requestDTO.setDestinoLongitude(-46.6583);

        cliente = new Usuario();
        cliente.setId(1L);

        corrida = new Corrida();
        corrida.setOrigemLatitude(requestDTO.getOrigemLatitude());
        corrida.setOrigemLongitude(requestDTO.getOrigemLongitude());
        corrida.setDestinoLatitude(requestDTO.getDestinoLatitude());
        corrida.setDestinoLongitude(requestDTO.getDestinoLongitude());
    }

    @Test
    @DisplayName("Deve solicitar corrida com cálculo de rota e persistir")
    void deveSolicitarCorridaComSucesso() {
        // GIVEN
        RouteData routeData = new RouteData(5234, 742);
        when(corridaMapper.toEntity(requestDTO)).thenReturn(corrida);
        when(usuarioRepositoryPort.findById(1L)).thenReturn(Optional.of(cliente));
        when(routePort.calcularRota(any(GeoCoordinate.class), any(GeoCoordinate.class))).thenReturn(routeData);
        when(corridaService.salvar(any(Corrida.class))).thenReturn(corrida);
        when(corridaMapper.toResponseDTO(any(Corrida.class))).thenReturn(new CorridaResponseDTO());

        // WHEN
        facade.solicitar(requestDTO);

        // THEN
        verify(routePort, times(1)).calcularRota(
                argThat(c -> c.latitude().equals(-23.5505) && c.longitude().equals(-46.6333)),
                argThat(c -> c.latitude().equals(-23.5596) && c.longitude().equals(-46.6583))
        );
        assertThat(corrida.getDistanciaEstimadaMetros()).isEqualTo(5234);
        assertThat(corrida.getDuracaoEstimadaSegundos()).isEqualTo(742);
        verify(corridaService, times(1)).salvar(corrida);
    }

    @Test
    @DisplayName("Deve falhar solicitação quando cálculo de rota falha")
    void deveFalharQuandoCalculoRotaFalha() {
        // GIVEN
        when(corridaMapper.toEntity(requestDTO)).thenReturn(corrida);
        when(usuarioRepositoryPort.findById(1L)).thenReturn(Optional.of(cliente));
        when(routePort.calcularRota(any(), any())).thenThrow(new RouteCalculationException("Erro Google"));

        // WHEN & THEN
        assertThatThrownBy(() -> facade.solicitar(requestDTO))
                .isInstanceOf(RouteCalculationException.class);

        verify(corridaService, never()).salvar(any());
    }
}
