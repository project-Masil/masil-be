package com.masil.backend.util.Jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.masil.backend.util.Redis.RedisUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private RedisUtil redisUtil;

    // 토큰 유효시간
    private static final long MAX_EXPIRATION = Long.MAX_VALUE / 1000;

    private Key getSigningKey() {
    	byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("The key must be at least 256 bits (32 bytes) long.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public void addToBlacklist(String token) {
        // 블랙리스트에 토큰 추가
        redisUtil.addToBlacklist(token);
    }

    public boolean isTokenBlacklisted(String token) {
        // 블랙리스트에 있는지 확인
        return redisUtil.isBlacklisted(token);
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateAccessToken(UserDetails userDetails, String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return createToken(claims, userDetails.getUsername(), MAX_EXPIRATION);
    }

    public String generateRefreshToken(UserDetails userDetails, String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return createToken(claims, userDetails.getUsername(), MAX_EXPIRATION);
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
    	long now = System.currentTimeMillis();
        long expirationTimeInMillis = expirationTime * 1000; // milliseconds

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String userId = extractUserId(token);
        return (userId.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public long getAccessTokenValidity() {
        return MAX_EXPIRATION;
    }

    public long getRefreshTokenValidity() {
        return MAX_EXPIRATION;
    }
}
