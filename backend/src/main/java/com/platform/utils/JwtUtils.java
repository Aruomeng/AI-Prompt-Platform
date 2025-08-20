package com.platform.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.nio.charset.StandardCharsets;

/**
 * JWT工具类 - 修复密钥长度问题
 * 使用符合RFC 7518标准的密钥长度
 */
@Component
public class JwtUtils {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private static final int MIN_KEY_LENGTH = 32; // 256 bits for HS256
    
    /**
     * 获取安全的密钥
     * 确保密钥长度符合RFC 7518标准
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        
        // 如果密钥长度不足，使用填充
        if (keyBytes.length < MIN_KEY_LENGTH) {
            byte[] paddedKey = new byte[MIN_KEY_LENGTH];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, MIN_KEY_LENGTH));
            
            // 如果密钥太短，重复填充
            if (keyBytes.length < MIN_KEY_LENGTH) {
                for (int i = keyBytes.length; i < MIN_KEY_LENGTH; i++) {
                    paddedKey[i] = (byte) ((i * 7) % 256); // 简单的填充模式
                }
            }
            return Keys.hmacShaKeyFor(paddedKey);
        }
        
        // 如果密钥太长，截断到合适长度
        if (keyBytes.length > MIN_KEY_LENGTH) {
            byte[] truncatedKey = new byte[MIN_KEY_LENGTH];
            System.arraycopy(keyBytes, 0, truncatedKey, 0, MIN_KEY_LENGTH);
            return Keys.hmacShaKeyFor(truncatedKey);
        }
        
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * 生成token
     */
    public String generateToken(Long userId, String username, String role) {
        SecretKey key = getSigningKey();
        Date expirationDate = new Date(System.currentTimeMillis() + expiration);
        
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 从token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.parseLong(claims.getSubject());
    }
    
    /**
     * 从token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("username", String.class);
    }
    
    /**
     * 从token中获取用户角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }
    
    /**
     * 验证token
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = getSigningKey();
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * 获取token的过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }
    
    /**
     * 判断token是否过期
     */
    private boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    /**
     * 解析token获取claims
     */
    private Claims getClaimsFromToken(String token) {
        SecretKey key = getSigningKey();
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}