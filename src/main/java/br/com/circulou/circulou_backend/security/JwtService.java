package br.com.circulou.circulou_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@org.springframework.beans.factory.annotation.Value("${circulou.security.jwt.secret:}") String secret,
                      Environment env) {
        
        boolean isProd = Arrays.asList(env.getActiveProfiles()).contains("prod");
        
        if (isProd && (secret == null || secret.isBlank())) {
            throw new IllegalStateException("JWT_SECRET não configurado. O sistema não pode iniciar em ambiente de produção sem uma chave de segurança válida.");
        }

        // Fallback apenas para desenvolvimento, se não houver secret configurado
        String finalSecret = (secret == null || secret.isBlank()) 
            ? "circulou-dev-fallback-secret-key-com-pelo-menos-32-caracteres" 
            : secret;

        this.secretKey = Keys.hmacShaKeyFor(finalSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String gerarToken(String email) {
        long agora = System.currentTimeMillis();

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(agora))
                .expiration(new Date(agora + 1000 * 60 * 60))
                .signWith(secretKey)
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    public boolean tokenValido(String token, String email) {
        String emailToken = extrairEmail(token);
        return emailToken.equals(email) && !tokenExpirado(token);
    }

    private boolean tokenExpirado(String token) {
        return extrairClaims(token).getExpiration().before(new Date());
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
