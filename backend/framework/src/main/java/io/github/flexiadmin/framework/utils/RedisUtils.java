package io.github.flexiadmin.framework.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 缓存用户信息，key: user:{username}
    public void cacheUserInfo(String username, Object userInfo, long expiration) {
        redisTemplate.opsForValue().set("user:" + username, userInfo, expiration, TimeUnit.SECONDS);
    }

    // 获取缓存的用户信息
    public Object getUserInfo(String username) {
        return redisTemplate.opsForValue().get("user:" + username);
    }

    // 缓存用户权限，key: user:{username}:permissions
    public void cacheUserPermissions(String username, Object permissions, long expiration) {
        redisTemplate.opsForValue().set("user:" + username + ":permissions", permissions, expiration, TimeUnit.SECONDS);
    }

    // 获取缓存的用户权限
    public Object getUserPermissions(String username) {
        return redisTemplate.opsForValue().get("user:" + username + ":permissions");
    }

    // 将 token 加入黑名单
    public void addTokenToBlacklist(String token, long expiration) {
        redisTemplate.opsForValue().set("token:blacklist:" + token, "1", expiration, TimeUnit.SECONDS);
    }

    // 检查 token 是否在黑名单中
    public boolean isTokenInBlacklist(String token) {
        return redisTemplate.hasKey("token:blacklist:" + token);
    }

    // 移除缓存的用户信息
    public void removeUserInfo(String username) {
        redisTemplate.delete("user:" + username);
    }

    // 移除缓存的用户权限
    public void removeUserPermissions(String username) {
        redisTemplate.delete("user:" + username + ":permissions");
    }

    // 刷新缓存过期时间
    public void refreshCacheExpiration(String key, long expiration) {
        redisTemplate.expire(key, expiration, TimeUnit.SECONDS);
    }
}