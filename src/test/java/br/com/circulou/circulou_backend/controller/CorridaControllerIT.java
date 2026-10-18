package br.com.circulou.circulou_backend.controller;

import br.com.circulou.circulou_backend.dto.CorridaRequestDTO;
import br.com.circulou.circulou_backend.dto.CorridaResponseDTO;
import br.com.circulou.circulou_backend.dto.UsuarioRequestDTO;
import br.com.circulou.circulou_backend.integration.BaseIntegrationTest;
import br.com.circulou.circulou_backend.model.*;
import br.com.circulou.circulou_backend.port.out.MotoristaProfileRepositoryPort;
import br.com.circulou.circulou_backend.port.out.RouteData;
import br.com.circulou.circulou_backend.port.out.RoutePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CorridaControllerIT extends BaseIntegrationTest {

    @Autowired
    private MotoristaProfileRepositoryPort motoristaRepositoryPort;

    @MockitoBean
    private RoutePort routePort;

    private String token;
    private Long clienteId;
    private Long motoristaId;

    @BeforeEach
    void setUp() throws Exception {
        // Mock default behavior for routePort
        when(routePort.calcularRota(any(), any())).thenReturn(new RouteData(5234, 742, "encoded-polyline-test"));

        // Criar Cliente
        token = criarUsuarioEObterToken("cliente.mobilidade@test.com");
        clienteId = usuarioRepositoryPort.findByEmail("cliente.mobilidade@test.com").get().getId();

        // Criar Motorista (Entidade Manual para o Profile)
        UsuarioRequestDTO motoristaUserDTO = new UsuarioRequestDTO();
        motoristaUserDTO.setNome("Motorista Teste");
        motoristaUserDTO.setEmail("motorista@test.com");
        motoristaUserDTO.setSenha("123456");
        motoristaUserDTO.setTelefone("11888887777");
        motoristaUserDTO.setRole("USER");
        motoristaUserDTO.setAtivo(true);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(motoristaUserDTO)))
                .andExpect(status().isOk());

        Usuario motoristaUser = usuarioRepositoryPort.findByEmail("motorista@test.com").get();
        
        MotoristaProfile profile = new MotoristaProfile();
        profile.setUsuario(motoristaUser);
        profile.setCnh("12345678901");
        profile.setCategoriaCnh("B");
        profile.setStatusPerfil(StatusPerfil.ATIVO);
        profile.setStatusDocumentacao(StatusDocumentacao.APROVADO);
        profile.setRatingMedia(BigDecimal.valueOf(5.0));
        
        motoristaId = motoristaRepositoryPort.save(profile).getId();
    }

    @Test
    @DisplayName("Deve realizar o fluxo completo da corrida")
    void deveRealizarFluxoCompletoCorrida() throws Exception {
        // 1. Solicitar Corrida
        CorridaRequestDTO requestDTO = new CorridaRequestDTO();
        requestDTO.setClienteId(clienteId);
        requestDTO.setOrigemLogradouro("Rua A");
        requestDTO.setOrigemNumero("100");
        requestDTO.setOrigemLatitude(-23.5505);
        requestDTO.setOrigemLongitude(-46.6333);
        requestDTO.setDestinoLogradouro("Rua B");
        requestDTO.setDestinoNumero("200");
        requestDTO.setDestinoLatitude(-23.5596);
        requestDTO.setDestinoLongitude(-46.6583);
        requestDTO.setValorEstimado(new BigDecimal("30.00"));

        MvcResult resPost = mockMvc.perform(post("/corridas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distanciaMetros").value(5234))
                .andExpect(jsonPath("$.duracaoSegundos").value(742))
                .andExpect(jsonPath("$.geometriaRota").value("encoded-polyline-test"))
                .andReturn();

        CorridaResponseDTO corrida = objectMapper.readValue(resPost.getResponse().getContentAsString(), CorridaResponseDTO.class);
        Long corridaId = corrida.getId();
        assertThat(corrida.getStatus()).isEqualTo(CorridaStatus.SOLICITADA);

        // 2. Aceitar Corrida
        mockMvc.perform(patch("/corridas/" + corridaId + "/aceitar")
                        .header("Authorization", token)
                        .param("motoristaId", motoristaId.toString()))
                .andExpect(status().isOk());
        verificarStatus(corridaId, CorridaStatus.ACEITA);

        // 3. Iniciar Caminho
        mockMvc.perform(patch("/corridas/" + corridaId + "/iniciar-caminho")
                        .header("Authorization", token))
                .andExpect(status().isOk());
        verificarStatus(corridaId, CorridaStatus.MOTORISTA_A_CAMINHO);

        // 4. Embarcar
        mockMvc.perform(patch("/corridas/" + corridaId + "/embarcar")
                        .header("Authorization", token))
                .andExpect(status().isOk());
        verificarStatus(corridaId, CorridaStatus.CLIENTE_EMBARCADO);

        // 5. Parada na Loja
        mockMvc.perform(patch("/corridas/" + corridaId + "/parada-loja")
                        .header("Authorization", token))
                .andExpect(status().isOk());
        verificarStatus(corridaId, CorridaStatus.PARADA_NA_LOJA);

        // 6. Continuar
        mockMvc.perform(patch("/corridas/" + corridaId + "/continuar")
                        .header("Authorization", token))
                .andExpect(status().isOk());
        verificarStatus(corridaId, CorridaStatus.EM_DESLOCAMENTO);

        // 7. Finalizar
        mockMvc.perform(patch("/corridas/" + corridaId + "/finalizar")
                        .header("Authorization", token))
                .andExpect(status().isOk());
        verificarStatus(corridaId, CorridaStatus.FINALIZADA);
    }

    @Test
    @DisplayName("Deve falhar ao tentar transição de status inválida")
    void deveFalharTransicaoInvalida() throws Exception {
        // 1. Solicitar Corrida
        CorridaRequestDTO requestDTO = new CorridaRequestDTO();
        requestDTO.setClienteId(clienteId);
        requestDTO.setOrigemLogradouro("Rua A");
        requestDTO.setOrigemNumero("100");
        requestDTO.setOrigemLatitude(-23.5505);
        requestDTO.setOrigemLongitude(-46.6333);
        requestDTO.setDestinoLogradouro("Rua B");
        requestDTO.setDestinoNumero("200");
        requestDTO.setDestinoLatitude(-23.5596);
        requestDTO.setDestinoLongitude(-46.6583);
        requestDTO.setValorEstimado(new BigDecimal("30.00"));

        MvcResult resPost = mockMvc.perform(post("/corridas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        CorridaResponseDTO corrida = objectMapper.readValue(resPost.getResponse().getContentAsString(), CorridaResponseDTO.class);
        Long corridaId = corrida.getId();

        // Tentar finalizar direto da solicitada (Inválido)
        mockMvc.perform(patch("/corridas/" + corridaId + "/finalizar")
                        .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar corrida inexistente")
    void deveRetornar404BuscaInexistente() throws Exception {
        mockMvc.perform(get("/corridas/999999")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    private void verificarStatus(Long id, CorridaStatus esperado) throws Exception {
        mockMvc.perform(get("/corridas/" + id)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    CorridaResponseDTO c = objectMapper.readValue(result.getResponse().getContentAsString(), CorridaResponseDTO.class);
                    assertThat(c.getStatus()).isEqualTo(esperado);
                });
    }

    @Test
    @DisplayName("Deve falhar ao solicitar corrida com coordenadas ausentes")
    void deveFalharCoordenadasAusentes() throws Exception {
        CorridaRequestDTO requestDTO = new CorridaRequestDTO();
        requestDTO.setClienteId(clienteId);
        // Coordenadas propositalmente ausentes

        mockMvc.perform(post("/corridas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve falhar ao solicitar corrida com latitude inválida")
    void deveFalharLatitudeInvalida() throws Exception {
        CorridaRequestDTO requestDTO = new CorridaRequestDTO();
        requestDTO.setClienteId(clienteId);
        requestDTO.setOrigemLatitude(95.0); // Inválido (> 90)
        requestDTO.setOrigemLongitude(-46.6333);
        requestDTO.setDestinoLatitude(-23.5596);
        requestDTO.setDestinoLongitude(-46.6583);

        mockMvc.perform(post("/corridas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve falhar ao solicitar corrida com longitude inválida")
    void deveFalharLongitudeInvalida() throws Exception {
        CorridaRequestDTO requestDTO = new CorridaRequestDTO();
        requestDTO.setClienteId(clienteId);
        requestDTO.setOrigemLatitude(-23.5505);
        requestDTO.setOrigemLongitude(-190.0); // Inválido (< -180)
        requestDTO.setDestinoLatitude(-23.5596);
        requestDTO.setDestinoLongitude(-46.6583);

        mockMvc.perform(post("/corridas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
}
