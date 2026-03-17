package io.github.zmxckj.flexiadmin.security;

import io.github.zmxckj.flexiadmin.entity.Appid;
import io.github.zmxckj.flexiadmin.service.AppidService;
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
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AppIdAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = Logger.getLogger(AppIdAuthenticationFilter.class.getName());
    private final AppidService appidService;

    public AppIdAuthenticationFilter(AppidService appidService) {
        this.appidService = appidService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 从请求头获取appid和签名
        String appId = request.getHeader("X-App-Id");
        String signature = request.getHeader("X-Signature");
        String timestamp = request.getHeader("X-Timestamp");
        String nonce = request.getHeader("X-Nonce");

        if (appId != null && signature != null && timestamp != null && nonce != null) {
            try {
                // 验证签名
                if (validateSignature(appId, signature, timestamp, nonce, request)) {
                    // 构建认证信息
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_APP"));
                    
                    // 获取应用权限
                    Appid appid = appidService.findByAppId(appId);
                    if (appid != null && appid.getPermissions() != null) {
                        String[] permissions = appid.getPermissions().split(",");
                        for (String permission : permissions) {
                            if (!permission.trim().isEmpty()) {
                                authorities.add(new SimpleGrantedAuthority(permission.trim()));
                            }
                        }
                    }
                    
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            appId, null, authorities
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                logger.info("AppId authentication failed: " + e.getMessage());
                // 不直接返回错误，继续执行过滤器链
            }
        }

        chain.doFilter(request, response);
    }

    private boolean validateSignature(String appId, String signature, String timestamp, String nonce, HttpServletRequest request) throws Exception {
        // 从服务中获取appid信息
        Appid appid = appidService.findByAppId(appId);
        if (appid == null || !appid.getStatus()) {
            return false;
        }

        // 验证时间戳，防止重放攻击
        long currentTime = System.currentTimeMillis() / 1000;
        long requestTime = Long.parseLong(timestamp);
        if (Math.abs(currentTime - requestTime) > 300) { // 5分钟内有效
            return false;
        }

        // 构建签名内容
        String content = appId + timestamp + nonce + getRequestContent(request);
        
        // 使用HMAC-SHA256生成签名
        String expectedSignature = generateHMACSHA256(content, appid.getSecret());

        // 比较生成的签名与请求中的签名
        return expectedSignature.equals(signature);
    }

    private String getRequestContent(HttpServletRequest request) throws IOException {
        // 获取请求体内容
        StringBuilder content = new StringBuilder();
        if ("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod())) {
            InputStream inputStream = request.getInputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                content.append(new String(buffer, 0, len));
            }
        } else if ("GET".equals(request.getMethod()) || "DELETE".equals(request.getMethod())) {
            // 对于GET和DELETE请求，使用查询参数作为签名内容
            content.append(request.getQueryString() != null ? request.getQueryString() : "");
        }
        return content.toString();
    }

    private String generateHMACSHA256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] bytes = mac.doFinal(data.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(bytes);
    }
}