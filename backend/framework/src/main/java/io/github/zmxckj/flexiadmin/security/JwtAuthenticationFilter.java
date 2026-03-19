package io.github.zmxckj.flexiadmin.security;

import io.github.zmxckj.flexiadmin.entity.Menu;
import io.github.zmxckj.flexiadmin.service.MenuService;
import io.github.zmxckj.flexiadmin.service.UserService;
import io.github.zmxckj.flexiadmin.utils.JwtUtils;
import io.github.zmxckj.flexiadmin.utils.RedisUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final RedisUtils redisUtils;
    private final UserService userService;
    private final MenuService menuService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, RedisUtils redisUtils, UserService userService, MenuService menuService) {
        this.jwtUtils = jwtUtils;
        this.redisUtils = redisUtils;
        this.userService = userService;
        this.menuService = menuService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                String username = jwtUtils.validateToken(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 检查 Redis 中是否有用户信息缓存
                    Object cachedUserInfo = redisUtils.getUserInfo(username);
                    if (cachedUserInfo == null) {
                        // 如果缓存中没有用户信息，从数据库中获取并缓存
                        // 这里可以调用 UserService 查询用户信息，然后缓存到 Redis
                    }

                    // 构建用户权限列表
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    List<String> authorityStrings = new ArrayList<>();
                    List<String> roles = new ArrayList<>();
                    Long userId = null;
                    
                    if (cachedUserInfo instanceof Map) {
                        Map<?, ?> userInfoMap = (Map<?, ?>) cachedUserInfo;
                        
                        // 获取用户ID
                        Object idObj = userInfoMap.get("id");
                        if (idObj != null) {
                            if (idObj instanceof Long) {
                                userId = (Long) idObj;
                            } else if (idObj instanceof Integer) {
                                userId = ((Integer) idObj).longValue();
                            } else if (idObj instanceof String) {
                                try {
                                    userId = Long.parseLong((String) idObj);
                                } catch (NumberFormatException e) {
                                    // 解析失败，使用默认值
                                }
                            }
                        }
                        
                        // 获取用户权限列表
                        Object permissionsObj = userInfoMap.get("permissions");
                        if (permissionsObj instanceof List) {
                            List<?> permissions = (List<?>) permissionsObj;
                            for (Object permission : permissions) {
                                if (permission instanceof String) {
                                    String perm = (String) permission;
                                    authorities.add(new SimpleGrantedAuthority(perm));
                                    authorityStrings.add(perm);
                                }
                            }
                        }
                        
                        // 获取用户角色列表
                        Object rolesObj = userInfoMap.get("roles");
                        if (rolesObj instanceof List) {
                            List<?> rolesList = (List<?>) rolesObj;
                            for (Object role : rolesList) {
                                if (role instanceof String) {
                                    roles.add((String) role);
                                }
                            }
                        }
                    } else {
                        // 如果用户信息缓存不存在，认为登录态失效
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"code\": 401, \"message\": \"登录态已失效，请重新登录\"}");
                        return;
                    }

                    // 创建 CustomUserDetails
                    CustomUserDetails userDetails = new CustomUserDetails(
                            userId != null ? userId : 0L, // 如果没有用户ID，使用默认值
                            username,
                            authorityStrings,
                            roles
                    );

                    // 使用 CustomUserDetails 作为 principal 创建认证 token
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                logger.error("JWT token validation failed: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"code\": 401, \"message\": \"Token 已过期或无效\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}