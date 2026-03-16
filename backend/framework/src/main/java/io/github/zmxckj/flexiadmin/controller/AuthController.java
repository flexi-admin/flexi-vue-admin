package io.github.zmxckj.flexiadmin.controller;

import io.github.zmxckj.flexiadmin.entity.Menu;
import io.github.zmxckj.flexiadmin.entity.User;
import io.github.zmxckj.flexiadmin.entity.LoginLog;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.dto.LoginDTO;
import io.github.zmxckj.flexiadmin.service.MenuService;
import io.github.zmxckj.flexiadmin.service.UserService;
import io.github.zmxckj.flexiadmin.service.LoginLogService;
import io.github.zmxckj.flexiadmin.utils.JwtUtils;
import io.github.zmxckj.flexiadmin.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final MenuService menuService;
    private final LoginLogService loginLogService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtils redisUtils;

    @Value("${flexi.jwt.expiration}")
    private long expiration;

    public AuthController(UserService userService, MenuService menuService, LoginLogService loginLogService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, RedisUtils redisUtils) {
        this.userService = userService;
        this.menuService = menuService;
        this.loginLogService = loginLogService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.redisUtils = redisUtils;
    }

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        String ip = getClientIp(request);

        User user = userService.findByUsername(username);
        if (user == null) {
            // 记录登录失败日志
            recordLoginLog(username, ip, false, "用户名或密码错误");
            return R.error(401, "用户名或密码错误");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // 记录登录失败日志
            recordLoginLog(username, ip, false, "用户名或密码错误");
            return R.error(401, "用户名或密码错误");
        }

        String token = jwtUtils.generateToken(username);
        
        // 构建不包含敏感信息的用户信息
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("status", user.getStatus());
        userInfo.put("createTime", user.getCreateTime());
        userInfo.put("updateTime", user.getUpdateTime());
        
        // 获取用户操作权限并添加到用户信息中
        List<Menu> operations = menuService.getOperationsByUserId(user.getId());
        List<String> operationCodes = new ArrayList<>();
        for (Menu operation : operations) {
            if (operation.getCode() != null) {
                operationCodes.add(operation.getCode());
            }
        }
        userInfo.put("permissions", operationCodes);
        
        // 缓存用户信息（包含权限）到 Redis
        redisUtils.cacheUserInfo(username, userInfo, expiration / 1000);

        // 记录登录成功日志
        recordLoginLog(username, ip, true, "登录成功");

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", userInfo);
        return R.success(response);
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    /**
     * 记录登录日志
     */
    private void recordLoginLog(String username, String ip, boolean status, String message) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUsername(username);
        loginLog.setIp(ip);
        loginLog.setStatus(status);
        loginLog.setMessage(message);
        loginLog.setCreateTime(java.time.LocalDateTime.now());
        loginLogService.save(loginLog);
    }

    @GetMapping("/user")
    public R<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        // 从请求头中获取Authorization token
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return R.error(401, "未授权，请先登录");
        }
        
        // 提取token
        String token = authorization.substring(7);
        // 从token中解析用户名
        String username = jwtUtils.getUsernameFromToken(token);
        
        if (username == null) {
            return R.error(401, "无效的token");
        }
        
        // 检查 Redis 中是否有用户信息缓存
        Object cachedUserInfo = redisUtils.getUserInfo(username);
        if (cachedUserInfo != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("user", cachedUserInfo);
            return R.success(response);
        }
        
        // 缓存中没有用户信息，从数据库中获取
        User user = userService.findByUsername(username);
        
        if (user == null) {
            return R.error(404, "用户不存在");
        }

        // 构建不包含敏感信息的用户信息
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("status", user.getStatus());
        userInfo.put("createTime", user.getCreateTime());
        userInfo.put("updateTime", user.getUpdateTime());

        // 缓存用户信息到 Redis
        redisUtils.cacheUserInfo(username, userInfo, expiration / 1000);

        Map<String, Object> response = new HashMap<>();
        response.put("user", userInfo);
        return R.success(response);
    }

    @PostMapping("/logout")
    public R<?> logout(HttpServletRequest request) {
        // 从请求头中获取Authorization token
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return R.error(401, "未授权，请先登录");
        }
        
        // 提取token
        String token = authorization.substring(7);
        
        try {
            // 登出，将 token 加入黑名单
            jwtUtils.logout(token);
            return R.success();
        } catch (Exception e) {
            return R.error(401, "无效的 token");
        }
    }

    @PostMapping("/refresh")
    public R<Map<String, Object>> refreshToken(HttpServletRequest request) {
        // 从请求头中获取Authorization token
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return R.error(401, "未授权，请先登录");
        }
        
        // 提取token
        String token = authorization.substring(7);
        
        try {
            // 刷新 token
            String newToken = jwtUtils.refreshToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", newToken);
            return R.success(response);
        } catch (Exception e) {
            return R.error(401, "无效的 token");
        }
    }
    

}