package io.github.zmxckj.flexiadmin.security;

import io.github.zmxckj.flexiadmin.dto.UserInfoDTO;
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
                    UserInfoDTO userInfo = redisUtils.getUserInfo(username);
                    if (userInfo == null) {
                        // 如果缓存中没有用户信息，认为登录态失效
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"code\": 401, \"message\": \"登录态已失效，请重新登录\"}");
                        return;
                    }

                    // 构建用户权限列表
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    List<String> authorityStrings = new ArrayList<>();
                    List<String> roles = new ArrayList<>();
                    Long userId = userInfo.getId();
                    Long tenantId = userInfo.getTenantId();
                    
                    // 获取用户权限列表
                    List<String> permissions = userInfo.getPermissions();
                    if (permissions != null) {
                        for (String permission : permissions) {
                            authorities.add(new SimpleGrantedAuthority(permission));
                            authorityStrings.add(permission);
                        }
                    }
                    
                    // 获取用户角色列表
                    List<String> userRoles = userInfo.getRoles();
                    if (userRoles != null) {
                        roles.addAll(userRoles);
                    }

                    // 创建 CustomUserDetails
                    CustomUserDetails userDetails = new CustomUserDetails(
                            userId,
                            username,
                            tenantId,
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