package com.petdiet.auth.client;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class EmailConfirmTokenUtil {

    private final byte[] secretBytes;
    private static final long EXPIRATION_MS = 86_400_000L; // 24시간

    public EmailConfirmTokenUtil(@Value("${jwt.secret}") String secret) {
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(secret);
        } catch (Exception e) {
            decoded = secret.getBytes();
        }
        this.secretBytes = decoded;
    }

    public String generate(UUID authUuid, String email, String userName) {
        return Jwts.builder()
                .subject(authUuid.toString())
                .claim("email", email)
                .claim("userName", userName)
                .claim("type", "email_confirm")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(Keys.hmacShaKeyFor(secretBytes))
                .compact();
    }

    public Claims verify(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretBytes))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!"email_confirm".equals(claims.get("type", String.class))) {
            throw new IllegalArgumentException("유효하지 않은 인증 토큰입니다.");
        }
        return claims;
    }
}
