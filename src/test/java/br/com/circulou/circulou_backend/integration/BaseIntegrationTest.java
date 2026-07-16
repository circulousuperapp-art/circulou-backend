package br.com.circulou.circulou_backend.integration;

import br.com.circulou.circulou_backend.config.TestcontainersConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.circulou.circulou_backend.auth.LoginRequest;
import br.com.circulou.circulou_backend.auth.LoginResponse;
import br.com.circulou.circulou_backend.dto.LojistaProfileRequestDTO;
import br.com.circulou.circulou_backend.dto.LojistaProfileResponseDTO;
import br.com.circulou.circulou_backend.dto.UsuarioRequestDTO;
import br.com.circulou.circulou_backend.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UsuarioRepositoryPort usuarioRepositoryPort;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearDatabase() {
        jdbcTemplate.execute("DELETE FROM item_pedido");
        jdbcTemplate.execute("DELETE FROM pedido");
        jdbcTemplate.execute("DELETE FROM oferta");
        jdbcTemplate.execute("DELETE FROM produto");
        jdbcTemplate.execute("DELETE FROM loja");
        jdbcTemplate.execute("DELETE FROM lojista_profile");
        jdbcTemplate.execute("DELETE FROM motorista_profile");
        jdbcTemplate.execute("DELETE FROM forma_pagamento");
        jdbcTemplate.execute("DELETE FROM historico_destino");
        jdbcTemplate.execute("DELETE FROM usuario");
        jdbcTemplate.execute("DELETE FROM endereco");
    }

    protected String obterTokenAutenticacao(String email, String senha) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setSenha(senha);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);
        return "Bearer " + loginResponse.getToken();
    }

    protected Long criarLojistaProfile(String token) throws Exception {
        LojistaProfileRequestDTO profileDTO = new LojistaProfileRequestDTO();
        profileDTO.setCnpj("12345678000199");
        profileDTO.setRazaoSocial("Loja Teste LTDA");
        profileDTO.setSegmento("Alimentos");

        MvcResult result = mockMvc.perform(post("/lojistas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDTO)))
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), LojistaProfileResponseDTO.class).getId();
    }

    protected String criarUsuarioEObterToken(String email) throws Exception {
        UsuarioRequestDTO usuarioDTO = new UsuarioRequestDTO();
        usuarioDTO.setNome("User Test");
        usuarioDTO.setEmail(email);
        usuarioDTO.setSenha("123456");
        usuarioDTO.setTelefone("11999998888");
        usuarioDTO.setRole("USER");
        usuarioDTO.setAtivo(true);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDTO)));

        return obterTokenAutenticacao(email, "123456");
    }

}
