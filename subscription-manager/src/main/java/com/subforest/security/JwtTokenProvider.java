package com.subforest.config;

import com.subforest.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long validityInMs = 1000L * 60 * 60; // 1시간

    // User 엔티티 기반 JWT 생성
    public String createToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMs);

        // roles가 없으므로 기본 ROLE_USER 하드코딩
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("roles", List.of("ROLE_USER"))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    public Long getUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    public List<String> getRoles(String token) {
        return getClaims(token).get("roles", List.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getRemainingExpiration(String token) {
        Date expiry = getClaims(token).getExpiration();
        return (expiry.getTime() - System.currentTimeMillis()) / 1000; // 초 단위
    }
}
