package com.platform.utils;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * JWT密钥生成器
 * 用于生成符合RFC 7518标准的HMAC-SHA密钥
 */
public class JwtKeyGenerator {
    
    /**
     * 生成符合RFC 7518标准的HS256密钥
     * @return Base64编码的密钥字符串
     */
    public static String generateHS256Key() {
        // HS256需要至少256位（32字节）的密钥
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    /**
     * 生成符合RFC 7518标准的HS512密钥
     * @return Base64编码的密钥字符串
     */
    public static String generateHS512Key() {
        // HS512需要至少512位（64字节）的密钥
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    /**
     * 生成自定义长度的安全密钥
     * @param keyLength 密钥长度（字节数）
     * @return Base64编码的密钥字符串
     */
    public static String generateSecureKey(int keyLength) {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[keyLength];
        random.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }
    
    public static void main(String[] args) {
        System.out.println("=== JWT密钥生成器 ===");
        System.out.println("HS256密钥 (32字节/256位): " + generateHS256Key());
        System.out.println("HS512密钥 (64字节/512位): " + generateHS512Key());
        System.out.println("自定义512位密钥: " + generateSecureKey(64));
    }
}