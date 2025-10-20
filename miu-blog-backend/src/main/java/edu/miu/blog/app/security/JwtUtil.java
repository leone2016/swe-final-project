package edu.miu.blog.app.security;


import edu.miu.blog.app.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;



@Component
@Slf4j
public class JwtUtil {

    private final SecretKey key;
    private final long EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7 días

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        log.info("Initializing JWT utility with secret key");
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); // ≥ 32 bytes
    }

    public String generateToken(User user) {
        log.debug("Generating JWT token for user: {}", user.getUsername());
        Date now = new Date();
        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        log.debug("JWT token generated successfully for user: {}", user.getUsername());
        return token;
    }

    public boolean isTokenValid(String token) {
        try {
            log.debug("Validating JWT token");
            extractAllClaims(token);
            log.debug("JWT token is valid");
            return true;
        } catch (JwtException e) { // expirado, firma inválida, etc.
            log.warn("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Claims extractAllClaims(String token) throws JwtException {
        log.debug("Extracting claims from JWT token");
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload(); // Claims
        log.debug("Successfully extracted claims from JWT token");
        return claims;
    }
}
