package com.autoscolombia.parqueadero.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Encapsula la creación y validación de tokens JWT (basado en RollerApp).
 */
@Component
public class JwtService {

    private final String secretKey;
    private final long expirationInMillis;
    private final String issuer;

    public JwtService(
            @Value("${autoscolombia.security.jwt.secret}") String secretKey,
            @Value("${autoscolombia.security.jwt.expiration}") long expirationInMillis,
            @Value("${autoscolombia.security.jwt.issuer}") String issuer) {
        this.secretKey = secretKey;
        this.expirationInMillis = expirationInMillis;
        this.issuer = issuer;
    }

    /**
     * Genera un token JWT incluyendo los roles como claim.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = Map.of(
                "roles",
                userDetails.getAuthorities()
                        .stream()
                        .map(authority -> authority.getAuthority())
                        .collect(Collectors.toList()));

        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationInMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae el identificador (subject) del token.
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Valida si el token es vigente y corresponde al usuario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getExpirationInMillis() {
        return expirationInMillis;
    }
}
