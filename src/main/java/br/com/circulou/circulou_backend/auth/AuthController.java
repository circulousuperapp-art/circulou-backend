package br.com.circulou.circulou_backend.auth;

import br.com.circulou.circulou_backend.dto.ErrorResponseDTO;
import br.com.circulou.circulou_backend.exception.BusinessException;
import br.com.circulou.circulou_backend.model.Usuario;
import br.com.circulou.circulou_backend.port.out.UsuarioRepositoryPort;
import br.com.circulou.circulou_backend.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para login e obtenção de token JWT")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UsuarioRepositoryPort usuarioRepositoryPort,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Valida as credenciais do usuário (e-mail e senha) e retorna um token JWT válido para acesso aos recursos protegidos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "E-mail ou senha incorretos ou dados malformatados", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno no processo de autenticação", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        logger.info("Tentativa de login para: {}", mascararEmail(request.getEmail()));

        Usuario usuario = usuarioRepositoryPort.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado: {}", mascararEmail(request.getEmail()));
                    return new BusinessException("E-mail ou senha inválidos");
                });

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            logger.warn("Senha inválida para: {}", mascararEmail(request.getEmail()));
            throw new BusinessException("E-mail ou senha inválidos");
        }

        String token = jwtService.gerarToken(usuario.getEmail());
        logger.info("Login bem-sucedido para: {}", mascararEmail(request.getEmail()));

        return new LoginResponse(token);
    }

    private String mascararEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            return "***" + email.substring(atIndex);
        }
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }
}
