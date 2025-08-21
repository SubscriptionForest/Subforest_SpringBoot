package com.subforest.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs = 1000L * 60 * 60 * 24; // 24시간

    // spring.jwt.secret 에 Base64 인코딩된 시크릿을 넣어둠
    public JwtUtil(@Value("${spring.jwt.secret}") String base64Secret) {
        // 앞뒤 공백/개행 제거 후 디코딩
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret.strip());
        this.key = Keys.hmacShaKeyFor(keyBytes); // HS256 충분한 길이 보장

        // 임시 진단 로그: 같은 서버/재시작에도 항상 동일해야 함
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            var fp = java.util.HexFormat.of().formatHex(md.digest(keyBytes));
            org.slf4j.LoggerFactory.getLogger(JwtUtil.class)
                .info("JWT key fingerprint(SHA-256, first16)={}", fp.substring(0, 16));
        } catch (Exception ignore) {}
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 유효하지 않음
            return false;
        }
    }
}
