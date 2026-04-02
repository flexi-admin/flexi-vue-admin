package io.github.zmxckj.flexiadmin.controller;

import io.github.zmxckj.flexiadmin.entity.*;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.dto.LoginDTO;
import io.github.zmxckj.flexiadmin.dto.UserInfoDTO;
import io.github.zmxckj.flexiadmin.service.MenuService;
import io.github.zmxckj.flexiadmin.service.UserService;
import io.github.zmxckj.flexiadmin.service.LoginLogService;
import io.github.zmxckj.flexiadmin.service.UserRoleService;
import io.github.zmxckj.flexiadmin.service.RoleService;
import io.github.zmxckj.flexiadmin.service.TenantService;
import io.github.zmxckj.flexiadmin.service.ConfigService;
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
    private final UserRoleService userRoleService;
    private final RoleService roleService;
    private final TenantService tenantService;
    private final ConfigService configService;

    @Value("${flexi.jwt.expiration}")
    private long expiration;

    public AuthController(UserService userService, MenuService menuService, LoginLogService loginLogService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, RedisUtils redisUtils, UserRoleService userRoleService, RoleService roleService, TenantService tenantService, ConfigService configService) {
        this.userService = userService;
        this.menuService = menuService;
        this.loginLogService = loginLogService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.redisUtils = redisUtils;
        this.userRoleService = userRoleService;
        this.roleService = roleService;
        this.tenantService = tenantService;
        this.configService = configService;
    }

    // 获取系统配置值
    private String getConfigValue(String key, String defaultValue) {
        try {
            Config config = configService.getOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Config>().eq("config_key", key));
            return config != null ? config.getValue() : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        Long tenantId = loginDTO.getTenantId();
        String ip = getClientIp(request);

        // 检查是否启用多租户
        boolean multiTenantEnabled = Boolean.parseBoolean(getConfigValue("system.multi_tenant_enabled", "false"));

        User user;
        if (multiTenantEnabled) {
            // 启用多租户，使用租户ID查询
            user = userService.findByUsernameAndTenantId(username, tenantId);
        } else {
            // 不启用多租户，使用原方法查询
            user = userService.findByUsername(username);
        }

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
        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setTenantId(user.getTenantId());
        userInfo.setStatus(user.getStatus());
        userInfo.setCreateTime(user.getCreateTime());
        userInfo.setUpdateTime(user.getUpdateTime());
        
        // 获取用户操作权限并添加到用户信息中
        List<Menu> operations = menuService.getOperationsByUserId(user.getId());
        List<String> operationCodes = new ArrayList<>();
        for (Menu operation : operations) {
            if (operation.getCode() != null) {
                operationCodes.add(operation.getCode());
            }
        }
        userInfo.setPermissions(operationCodes);
        
        // 获取用户角色列表并添加到用户信息中
        List<String> roles = new ArrayList<>();
        List<UserRole> userRoles = userRoleService.list(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserRole>().eq("user_id", user.getId()));
        for (UserRole userRole : userRoles) {
            Role role = roleService.getById(userRole.getRoleId());
            if (role != null) {
                roles.add(role.getName());
            }
        }
        userInfo.setRoles(roles);
        
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
        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setTenantId(user.getTenantId());
        userInfo.setStatus(user.getStatus());
        userInfo.setCreateTime(user.getCreateTime());
        userInfo.setUpdateTime(user.getUpdateTime());
        
        // 获取用户操作权限并添加到用户信息中
        List<Menu> operations = menuService.getOperationsByUserId(user.getId());
        List<String> operationCodes = new ArrayList<>();
        for (Menu operation : operations) {
            if (operation.getCode() != null) {
                operationCodes.add(operation.getCode());
            }
        }
        userInfo.setPermissions(operationCodes);
        
        // 获取用户角色列表并添加到用户信息中
        List<String> roles = new ArrayList<>();
        List<UserRole> userRoles = userRoleService.list(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserRole>().eq("user_id", user.getId()));
        for (UserRole userRole : userRoles) {
            Role role = roleService.getById(userRole.getRoleId());
            if (role != null) {
                roles.add(role.getName());
            }
        }
        userInfo.setRoles(roles);

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
    
    /**
     * 获取所有租户列表
     */
    @GetMapping("/tenants")
    public R<List<Tenant>> getTenants() {
        List<Tenant> tenants = tenantService.list();
        return R.success(tenants);
    }
    
    /**
     * 获取系统配置
     */
    @GetMapping("/config")
    public R<Map<String, String>> getConfig() {
        Map<String, String> config = new HashMap<>();
        // 检查是否启用多租户
        config.put("multiTenantEnabled", getConfigValue("system.multi_tenant_enabled", "false"));
        return R.success(config);
    }

}