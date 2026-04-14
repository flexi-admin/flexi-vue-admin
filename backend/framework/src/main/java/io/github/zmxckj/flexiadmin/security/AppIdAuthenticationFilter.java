package io.github.zmxckj.flexiadmin.security;

import io.github.zmxckj.flexiadmin.entity.Appid;
import io.github.zmxckj.flexiadmin.security.CustomUserDetails;
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
        String signMethod = request.getHeader("X-Sign-Method");

        if (appId != null && signature != null && timestamp != null && nonce != null) {
            try {
                // 验证签名
                if (validateSignature(appId, signature, timestamp, nonce, signMethod, request)) {
                    // 构建认证信息
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                List<String> authorityStrings = new ArrayList<>();
                List<String> roles = new ArrayList<>();
                
                // 添加应用角色
                String appRole = "ROLE_APP";
                authorities.add(new SimpleGrantedAuthority(appRole));
                authorityStrings.add(appRole);
                
                // 获取应用权限
                Appid appid = appidService.findByAppId(appId);
                if (appid != null && appid.getPermissions() != null) {
                    String[] permissions = appid.getPermissions().split(",");
                    for (String permission : permissions) {
                        if (!permission.trim().isEmpty()) {
                            authorities.add(new SimpleGrantedAuthority(permission.trim()));
                            authorityStrings.add(permission.trim());
                        }
                    }
                }
                
                // 创建 CustomUserDetails
                CustomUserDetails userDetails = new CustomUserDetails(
                        0L, // 应用ID，使用0作为默认值
                        appId, // 使用appId作为username
                        0L, // 租户ID，使用0作为默认值
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
                logger.info("AppId authentication failed: " + e.getMessage());
                // 不直接返回错误，继续执行过滤器链
            }
        }

        chain.doFilter(request, response);
    }

    private boolean validateSignature(String appId, String signature, String timestamp, String nonce, String signMethod, HttpServletRequest request) throws Exception {
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

        // 根据签名方法选择不同的验证方式
        if ("WX".equals(signMethod)) {
            // 微信小程序简化版本的签名验证
            return validateWXSignature(appId, signature, timestamp, nonce, appid.getSecret(), request);
        } else {
            // 标准HMAC-SHA256签名验证
            return validateStandardSignature(appId, signature, timestamp, nonce, appid.getSecret(), request);
        }
    }

    // 微信小程序简化版本的签名验证
    private boolean validateWXSignature(String appId, String signature, String timestamp, String nonce, String secret, HttpServletRequest request) throws Exception {
        // 验证时间戳，防止请求被重放
        long currentTimestamp = System.currentTimeMillis() / 1000;
        long requestTimestamp = Long.parseLong(timestamp);
        long timestampDiff = Math.abs(currentTimestamp - requestTimestamp);
        // 时间戳差异阈值：60s
        if (timestampDiff > 60) {
            return false;
        }
        
        // 构建签名内容：appId + timestamp + nonce + method + url + data
        StringBuilder content = new StringBuilder();
        // 使用request.getRequestURI()获取URL路径，不包含上下文路径
        String requestUri = request.getRequestURI();
        content.append(appId).append(timestamp).append(nonce).append(request.getMethod()).append(requestUri);
        
        // 如果有请求体数据，添加到签名内容中
        if ("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod())) {
            InputStream inputStream = request.getInputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                content.append(new String(buffer, 0, len));
            }
        } else if ("GET".equals(request.getMethod()) || "DELETE".equals(request.getMethod())) {
            // 对于GET和DELETE请求，使用查询参数作为签名内容
            String queryString = request.getQueryString();
            if (queryString != null && !queryString.isEmpty()) {
                // 对查询参数进行排序，确保与前端一致
                String[] params = queryString.split("&");
                java.util.Arrays.sort(params);
                StringBuilder sortedQueryString = new StringBuilder();
                for (int i = 0; i < params.length; i++) {
                    if (i > 0) {
                        sortedQueryString.append("&");
                    }
                    sortedQueryString.append(params[i]);
                }
                content.append(sortedQueryString.toString());
            } else {
                content.append("");
            }
        }
        
        // 使用简单的哈希算法生成签名
        String expectedContent = content.toString() + secret;
        // 使用简单的哈希算法
        int hash = 0;
        for (char c : expectedContent.toCharArray()) {
            hash = 31 * hash + c;
        }
        // 将哈希结果转换为十六进制字符串
        String expectedSignature = Integer.toHexString(hash);

        // 比较生成的签名与请求中的签名
        return expectedSignature.equals(signature);
    }

    // 标准HMAC-SHA256签名验证
    private boolean validateStandardSignature(String appId, String signature, String timestamp, String nonce, String secret, HttpServletRequest request) throws Exception {
        // 构建签名内容
        String content = appId + timestamp + nonce + getRequestContent(request);
        
        // 使用HMAC-SHA256生成签名
        String expectedSignature = generateHMACSHA256(content, secret);

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
            String queryString = request.getQueryString();
            if (queryString != null && !queryString.isEmpty()) {
                // 对查询参数进行排序，确保与前端一致
                String[] params = queryString.split("&");
                java.util.Arrays.sort(params);
                StringBuilder sortedQueryString = new StringBuilder();
                for (int i = 0; i < params.length; i++) {
                    if (i > 0) {
                        sortedQueryString.append("&");
                    }
                    sortedQueryString.append(params[i]);
                }
                content.append(sortedQueryString.toString());
            } else {
                content.append("");
            }
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
