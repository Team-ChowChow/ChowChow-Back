package com.petdiet.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final byte[] secretKeyBytes;
    private final String supabaseUrl;
    private final ObjectMapper objectMapper;

    private volatile PublicKey ecPublicKey;
    private volatile boolean ecKeyFetched = false;

    public JwtAuthenticationFilter(
            @Value("${supabase.jwt-secret}") String jwtSecret,
            @Value("${supabase.url}") String supabaseUrl,
            ObjectMapper objectMapper) {
        this.secretKeyBytes = decodeSecret(jwtSecret);
        this.supabaseUrl = supabaseUrl;
        this.objectMapper = objectMapper;
    }

    private static byte[] decodeSecret(String secret) {
        try {
            return Base64.getDecoder().decode(secret);
        } catch (Exception e) {
            return secret.getBytes();
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token != null) {
            try {
                Claims claims = parseToken(token);
                UUID authUuid = UUID.fromString(claims.getSubject());
                String email = claims.get("email", String.class);
                String name = extractName(claims);
                String avatarUrl = extractAvatarUrl(claims);

                SupabasePrincipal principal = new SupabasePrincipal(authUuid, email, name, avatarUrl);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        principal, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                log.debug("JWT 검증 실패: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private Claims parseToken(String token) {
        // ES256 (Supabase 신규 프로젝트 기본값) 먼저 시도
        PublicKey pk = getEcPublicKey();
        if (pk != null) {
            try {
                return Jwts.parser()
                        .verifyWith(pk)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
            } catch (Exception ignored) {
            }
        }
        // HS256 fallback
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKeyBytes))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private PublicKey getEcPublicKey() {
        if (!ecKeyFetched) {
            synchronized (this) {
                if (!ecKeyFetched) {
                    ecPublicKey = fetchEcPublicKey();
                    ecKeyFetched = true;
                }
            }
        }
        return ecPublicKey;
    }

    private PublicKey fetchEcPublicKey() {
        try {
            String jwksJson = WebClient.create(supabaseUrl)
                    .get()
                    .uri("/auth/v1/.well-known/jwks.json")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode keys = objectMapper.readTree(jwksJson).path("keys");
            if (keys.size() == 0) return null;

            JsonNode key = keys.get(0);
            if (!"EC".equals(key.path("kty").stringValue())) return null;

            byte[] xBytes = Base64.getUrlDecoder().decode(key.path("x").stringValue());
            byte[] yBytes = Base64.getUrlDecoder().decode(key.path("y").stringValue());

            ECPoint point = new ECPoint(new BigInteger(1, xBytes), new BigInteger(1, yBytes));
            AlgorithmParameters params = AlgorithmParameters.getInstance("EC");
            params.init(new ECGenParameterSpec("secp256r1"));
            ECParameterSpec ecSpec = params.getParameterSpec(ECParameterSpec.class);
            KeyFactory kf = KeyFactory.getInstance("EC");
            PublicKey publicKey = kf.generatePublic(new ECPublicKeySpec(point, ecSpec));
            log.info("Supabase JWKS에서 EC 공개키 로드 성공");
            return publicKey;
        } catch (Exception e) {
            log.warn("Supabase JWKS 조회 실패, HS256으로 fallback: {}", e.getMessage());
            return null;
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String extractName(Claims claims) {
        Map<String, Object> meta = (Map<String, Object>) claims.get("user_metadata");
        if (meta == null) return "";
        String name = (String) meta.get("full_name");
        if (name == null) name = (String) meta.get("name");
        return name != null ? name : "";
    }

    @SuppressWarnings("unchecked")
    private String extractAvatarUrl(Claims claims) {
        Map<String, Object> meta = (Map<String, Object>) claims.get("user_metadata");
        if (meta == null) return null;
        return (String) meta.get("avatar_url");
    }
}