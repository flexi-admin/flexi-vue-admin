package com.flexi.app.security;

import io.github.zmxckj.flexiadmin.entity.Appid;
import io.github.zmxckj.flexiadmin.service.AppidService;
import io.github.zmxckj.flexiadmin.security.AppIdAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AppIdAuthenticationTest {

    @Autowired
    private AppidService appidService;

    @Autowired
    private AppIdAuthenticationFilter appIdAuthenticationFilter;

    @Test
    public void testFindByAppId() {
        // 测试从数据库获取appid信息
        Appid appid = appidService.findByAppId("test");
        assertNotNull(appid);
        assertEquals("test", appid.getAppId());
        assertEquals("testsecret", appid.getSecret());
        assertEquals("测试应用", appid.getName());
        assertTrue(appid.getStatus());
        assertEquals("user:list,user:add", appid.getPermissions());

        // 测试从Redis缓存获取appid信息
        Appid cachedAppid = appidService.findByAppId("test");
        assertNotNull(cachedAppid);
        assertEquals("test", cachedAppid.getAppId());
    }

    @Test
    public void testGenerateSignature() throws Exception {
        // 测试签名生成
        String appId = "test";
        String secret = "testsecret";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = "123456";
        String content = appId + timestamp + nonce;

        // 生成签名
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] bytes = mac.doFinal(content.getBytes("UTF-8"));
        String signature = Base64.getEncoder().encodeToString(bytes);

        assertNotNull(signature);
        assertFalse(signature.isEmpty());
    }

    @Test
    public void testAppIdAuthentication() throws Exception {
        // 准备测试数据
        String appId = "test";
        String secret = "testsecret";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = "123456";
        String content = appId + timestamp + nonce;

        // 生成签名
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] bytes = mac.doFinal(content.getBytes("UTF-8"));
        String signature = Base64.getEncoder().encodeToString(bytes);

        // 创建模拟请求
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-App-Id", appId);
        request.addHeader("X-Signature", signature);
        request.addHeader("X-Timestamp", timestamp);
        request.addHeader("X-Nonce", nonce);
        request.setMethod("GET");

        // 创建模拟响应
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 清空SecurityContext
        SecurityContextHolder.clearContext();

        // 执行过滤器
        appIdAuthenticationFilter.doFilter(request, response, (req, res) -> {});

        // 验证认证成功
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(appId, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    public void testAppIdAuthenticationFailed() throws Exception {
        // 准备测试数据 - 使用错误的签名
        String appId = "test";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = "123456";
        String signature = "invalid-signature";

        // 创建模拟请求
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-App-Id", appId);
        request.addHeader("X-Signature", signature);
        request.addHeader("X-Timestamp", timestamp);
        request.addHeader("X-Nonce", nonce);
        request.setMethod("GET");

        // 创建模拟响应
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 清空SecurityContext
        SecurityContextHolder.clearContext();

        // 执行过滤器
        appIdAuthenticationFilter.doFilter(request, response, (req, res) -> {});

        // 验证认证失败
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testAppIdAuthenticationWithExpiredTimestamp() throws Exception {
        // 准备测试数据 - 使用过期的时间戳
        String appId = "test";
        String secret = "testsecret";
        String timestamp = String.valueOf((System.currentTimeMillis() / 1000) - 3600); // 1小时前
        String nonce = "123456";
        String content = appId + timestamp + nonce;

        // 生成签名
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] bytes = mac.doFinal(content.getBytes("UTF-8"));
        String signature = Base64.getEncoder().encodeToString(bytes);

        // 创建模拟请求
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-App-Id", appId);
        request.addHeader("X-Signature", signature);
        request.addHeader("X-Timestamp", timestamp);
        request.addHeader("X-Nonce", nonce);
        request.setMethod("GET");

        // 创建模拟响应
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 清空SecurityContext
        SecurityContextHolder.clearContext();

        // 执行过滤器
        appIdAuthenticationFilter.doFilter(request, response, (req, res) -> {});

        // 验证认证失败
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
