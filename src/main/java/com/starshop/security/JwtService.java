package com.starshop.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/** Sinh & xác thực JWT cho REST API + handshake WebSocket (F2.7). @owner: L. */
@Service
public class JwtService {

    private final SecretKey key;
    private final long accessMinutes;

    public JwtService(@Value("${starshop.jwt.secret}") String secret,
                      @Value("${starshop.jwt.access-token-minutes:30}") long accessMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessMinutes = accessMinutes;
    }

    /** Tạo access token mang subject = email + claim role. */
    public String generate(String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessMinutes * 60)))
                .signWith(key)
                .compact();
    }

    /** Xác thực & trả claims; ném exception nếu token sai/hết hạn. */
    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
