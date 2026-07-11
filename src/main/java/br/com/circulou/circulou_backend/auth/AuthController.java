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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para login e obtenção de token JWT")
public class AuthController {

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
    @Operation(summary = "Realizar login", description = "Autentica um usuário e retorna um token JWT válido por 1 hora")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "E-mail ou senha inválidos", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public LoginResponse login(@RequestBody LoginRequest request) {
        System.out.println("Tentativa de login para: " + request.getEmail());

        Usuario usuario = usuarioRepositoryPort.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    System.out.println("Usuário não encontrado: " + request.getEmail());
                    return new BusinessException("E-mail ou senha inválidos");
                });

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            System.out.println("Senha inválida para: " + request.getEmail());
            throw new BusinessException("E-mail ou senha inválidos");
        }

        String token = jwtService.gerarToken(usuario.getEmail());
        System.out.println("Login bem-sucedido para: " + request.getEmail());

        return new LoginResponse(token);
    }
}
