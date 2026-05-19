package br.com.raizes_do_nordeste.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import br.com.raizes_do_nordeste.infra.exceptions.JwtTokenInvalidoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class TokenConfig {

    private final Algorithm algorithm;
    private final String issuer;

    public TokenConfig(
            @Value("${security.jwt.secret:raizes-dev-secret-change-me}") String secret,
            @Value("${security.jwt.issuer:raizes-backend}") String issuer
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.issuer = issuer;
    }

    public String getSubject(String token) {
        try {
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException ex) {
            throw new JwtTokenInvalidoException("Token JWT invalido ou expirado.");
        }
    }

    public String gerarToken(UserDetails userDetails) {
        try {
            Instant now = Instant.now();
            Instant expiresAt = now.plus(2, ChronoUnit.HOURS);

            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(userDetails.getUsername())
                    .withIssuedAt(now)
                    .withExpiresAt(expiresAt)
                    .sign(algorithm);
        } catch (Exception ex) {
            throw new JwtTokenInvalidoException("Erro ao gerar token JWT.");
        }
    }

    public String getClaim(String token, String claimName) {
        try {
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getClaim(claimName)
                    .asString();
        } catch (JWTVerificationException ex) {
            throw new JwtTokenInvalidoException("Claim JWT invalida.");
        }
    }
}

