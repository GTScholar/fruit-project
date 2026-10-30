package com.fruitfresh.security;

import cn.hutool.core.date.DateUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类：负责 token 的生成与解析
 */
@Component
public class JwtUtil {

    @Value("${fruit.jwt.secret}")
    private String secret;

    @Value("${fruit.jwt.expire}")
    private Long expire;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param userType 用户类型 1普通用户 2管理员
     */
    public String generateToken(Long userId, String username, Integer userType) {
        Date now = new Date();
        Date expireDate = DateUtil.offsetSecond(now, expire.intValue());
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("userType", userType)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 token，返回 Claims；token 非法或过期时返回 null
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /** 从 token 中取用户名 */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims == null ? null : claims.getSubject();
    }

    /** 校验 token 是否有效（未过期且签名正确） */
    public boolean validateToken(String token) {
        Claims claims = parseToken(token);
        return claims != null && claims.getExpiration().after(new Date());
    }
}
