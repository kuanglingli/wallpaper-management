package com.wallpaper.management.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * JWT密钥
     */
    private static String secret;

    /**
     * JWT过期时间（秒）
     */
    private static long expire;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        JwtUtil.secret = secret;
        log.info("JWT密钥已设置，长度: {}", secret != null ? secret.length() : 0);
    }

    @Value("${jwt.expire}")
    public void setExpire(long expire) {
        JwtUtil.expire = expire;
        log.info("JWT过期时间已设置: {}秒", expire);
    }

    /**
     * 生成JWT令牌
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return JWT令牌
     */
    public static String generateToken(Long userId, String username) {
        log.info("开始生成JWT令牌 - userId: {}, username: {}", userId, username);
        
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expire * 1000);
        
        String token = JWT.create()
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withIssuedAt(now)
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC256(secret));
        
        log.info("JWT令牌生成成功，长度: {}", token.length());
        return token;
    }

    /**
     * 解析JWT令牌
     *
     * @param token JWT令牌
     * @return 解析后的JWT
     */
    public static DecodedJWT parseToken(String token) {
        if (token == null) {
            log.error("JWT解析失败：token为null");
            return null;
        }
        
        log.info("开始解析JWT令牌，token长度: {}", token.length());
        
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT jwt = verifier.verify(token);
            log.info("JWT解析成功");
            return jwt;
        } catch (JWTVerificationException e) {
            log.error("JWT解析失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 从JWT令牌中获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    public static Long getUserId(String token) {
        DecodedJWT jwt = parseToken(token);
        Long userId = jwt == null ? null : jwt.getClaim("userId").asLong();
        log.info("从JWT中获取userId: {}", userId);
        return userId;
    }

    /**
     * 从JWT令牌中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public static String getUsername(String token) {
        DecodedJWT jwt = parseToken(token);
        String username = jwt == null ? null : jwt.getClaim("username").asString();
        log.info("从JWT中获取username: {}", username);
        return username;
    }

    /**
     * 验证JWT令牌是否有效
     *
     * @param token JWT令牌
     * @return 是否有效
     */
    public static boolean verify(String token) {
        if (token == null) {
            log.error("JWT验证失败：token为null");
            return false;
        }
        
        log.info("开始验证JWT令牌，token长度: {}", token.length());
        boolean result = parseToken(token) != null;
        log.info("JWT验证结果: {}", result);
        return result;
    }
} 