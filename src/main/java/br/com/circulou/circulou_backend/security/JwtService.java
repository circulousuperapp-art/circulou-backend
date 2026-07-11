package br.com.circulou.circulou_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET = "circulou-super-app-chave-secreta-jwt-com-no-minimo-32-caracteres";

    private final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

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
