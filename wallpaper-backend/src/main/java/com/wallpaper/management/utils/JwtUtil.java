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
    }

    @Value("${jwt.expire}")
    public void setExpire(long expire) {
        JwtUtil.expire = expire;
    }

    /**
     * 生成JWT令牌
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return JWT令牌
     */
    public static String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expire * 1000);

        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withIssuedAt(now)
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 解析JWT令牌
     *
     * @param token JWT令牌
     * @return 解析后的JWT
     */
    public static DecodedJWT parseToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            return verifier.verify(token);
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
        return jwt == null ? null : jwt.getClaim("userId").asLong();
    }

    /**
     * 从JWT令牌中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public static String getUsername(String token) {
        DecodedJWT jwt = parseToken(token);
        return jwt == null ? null : jwt.getClaim("username").asString();
    }

    /**
     * 验证JWT令牌是否有效
     *
     * @param token JWT令牌
     * @return 是否有效
     */
    public static boolean verify(String token) {
        return parseToken(token) != null;
    }
} 