package io.github.zmxckj.flexiadmin.security;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class PermissionAspect {

    @Before("@annotation(io.github.zmxckj.flexiadmin.security.RequirePermission)")
    public void checkPermission(JoinPoint joinPoint) {
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("未授权访问");
        }

        // 获取方法上的 RequirePermission 注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);

        if (requirePermission != null) {
            String permission = requirePermission.value();
            // 检查用户是否拥有该权限
            if (!authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(permission))) {
                throw new SecurityException("权限不足");
            }
        }
    }
}