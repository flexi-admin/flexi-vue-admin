package io.github.zmxckj.flexiadmin.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.zmxckj.flexiadmin.dto.UserInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // 缓存用户信息，key: user:{username}
    public void cacheUserInfo(String username, UserInfoDTO userInfo, long expiration) {
        try {
            String jsonUserInfo = objectMapper.writeValueAsString(userInfo);
            redisTemplate.opsForValue().set("user:" + username, jsonUserInfo, expiration, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            // 序列化失败，直接抛出异常
            logger.error("Failed to serialize user info: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize user info", e);
        }
    }

    // 获取缓存的用户信息
    public UserInfoDTO getUserInfo(String username) {
        String cachedUserInfo = redisTemplate.opsForValue().get("user:" + username);
        if (cachedUserInfo == null) {
            return null;
        }
        
        try {
            // 解析JSON字符串为UserInfoDTO
            return objectMapper.readValue(cachedUserInfo, UserInfoDTO.class);
        } catch (Exception e) {
            logger.error("Failed to parse user info: {}", e.getMessage(), e);
            return null;
        }
    }

    // 缓存用户权限，key: user:{username}:permissions
    public void cacheUserPermissions(String username, Object permissions, long expiration) {
        try {
            String jsonPermissions = objectMapper.writeValueAsString(permissions);
            redisTemplate.opsForValue().set("user:" + username + ":permissions", jsonPermissions, expiration, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("Failed to serialize user permissions: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize user permissions", e);
        }
    }

    // 获取缓存的用户权限
    public Object getUserPermissions(String username) {
        String cachedPermissions = redisTemplate.opsForValue().get("user:" + username + ":permissions");
        if (cachedPermissions == null) {
            return null;
        }
        
        try {
            return objectMapper.readValue(cachedPermissions, Object.class);
        } catch (Exception e) {
            logger.error("Failed to parse user permissions: {}", e.getMessage(), e);
            return null;
        }
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

    // 通用设置缓存
    public void set(String key, Object value, long expiration, TimeUnit timeUnit) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, expiration, timeUnit);
        } catch (Exception e) {
            logger.error("Failed to serialize value: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize value", e);
        }
    }

    // 通用获取缓存
    public Object get(String key) {
        String cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue == null) {
            return null;
        }
        
        try {
            return objectMapper.readValue(cachedValue, Object.class);
        } catch (Exception e) {
            logger.error("Failed to parse value: {}", e.getMessage(), e);
            return null;
        }
    }
} 