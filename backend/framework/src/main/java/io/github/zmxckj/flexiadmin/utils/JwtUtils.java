package io.github.zmxckj.flexiadmin.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    @Value("${flexi.jwt.secret}")
    private String secret;

    @Value("${flexi.jwt.expiration}")
    private long expiration;

    @Autowired
    private RedisUtils redisUtils;

    public String generateToken(String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);

        String token = JWT.create()
                .withSubject(username)
                .withIssuedAt(now)
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC256(secret));

        return token;
    }

    public String validateToken(String token) throws JWTVerificationException {
        // 检查 token 是否在黑名单中
        if (redisUtils.isTokenInBlacklist(token)) {
            throw new JWTVerificationException("Token is in blacklist");
        }

        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);
        return decodedJWT.getSubject();
    }

    public String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getSubject();
    }

    // 刷新 token
    public String refreshToken(String token) throws JWTVerificationException {
        // 验证 token 是否有效
        String username = validateToken(token);
        // 生成新 token
        return generateToken(username);
    }

    // 登出，将 token 加入黑名单
    public void logout(String token) throws JWTVerificationException {
        // 验证 token 是否有效
        String username = validateToken(token);
        // 获取 token 过期时间
        DecodedJWT decodedJWT = JWT.decode(token);
        long expiresAt = decodedJWT.getExpiresAt().getTime();
        long now = System.currentTimeMillis();
        long remainingExpiration = (expiresAt - now) / 1000;

        if (remainingExpiration > 0) {
            // 将 token 加入黑名单
            redisUtils.addTokenToBlacklist(token, remainingExpiration);
            // 移除用户缓存
            redisUtils.removeUserInfo(username);
            redisUtils.removeUserPermissions(username);
        }
    }
}