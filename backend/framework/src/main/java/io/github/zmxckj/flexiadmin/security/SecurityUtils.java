package io.github.zmxckj.flexiadmin.security;

import io.github.zmxckj.flexiadmin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        SecurityUtils.userService = userService;
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                // 直接从CustomUserDetails中获取用户ID
                return ((CustomUserDetails) principal).getId();
            } else if (principal instanceof String) {
                // 兼容旧的实现，从数据库中获取
                String username = (String) principal;
                if (userService != null) {
                    return userService.findByUsername(username).getId();
                }
            }
        }
        return null;
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                // 从CustomUserDetails中获取用户名
                return ((CustomUserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                // 兼容旧的实现
                return (String) principal;
            }
        }
        return null;
    }
}