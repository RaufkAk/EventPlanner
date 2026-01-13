package com.yeditepe.config;

import jakarta.annotation.PostConstruct;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret:YeditepeEventPlannerProjectSecretKey2025VerySecure}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms:86400000}")
    private int jwtExpirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public JwtUtils() {
    }

    public String generateJwtToken(String username,
            java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> authorities) {
        java.util.List<String> roles = authorities.stream()
                .map(item -> item.getAuthority())
                .collect(java.util.stream.Collectors.toList());

        System.out.println("Generating JWT for user: " + username + " with roles: " + roles);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public java.util.List<String> getRolesFromJwtToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return (java.util.List<String>) claims.get("roles");
        } catch (Exception ex) {
            return java.util.Collections.emptyList();
        }
    }

    public String getUserNameFromJwtToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception ex) {
            return null;
        }
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}