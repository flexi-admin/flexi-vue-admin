# AppIdAuthenticationFilter 开发文档

## 概述

AppIdAuthenticationFilter 是 Spring Security 框架中的_once-per-request_过滤器，用于通过 AppId 和签名验证实现应用级别的身份认证。该过滤器适用于移动应用、小程序等需要应用身份验证的场景。

## 工作流程

```
请求 → 提取请求头 → 验证签名 → 构建认证信息 → 设置SecurityContext → 继续过滤器链
```

## 请求头说明

客户端请求时需要在 HTTP 请求头中携带以下字段：

| 请求头 | 必填 | 说明 |
|--------|------|------|
| X-App-Id | 是 | 应用唯一标识 |
| X-Signature | 是 | 请求签名 |
| X-Timestamp | 是 | 时间戳（秒级Unix时间戳） |
| X-Nonce | 是 | 随机字符串，用于防止重放攻击 |
| X-Sign-Method | 否 | 签名方法，支持 `WX`（微信小程序）和标准HMAC-SHA256，默认为标准方法 |

## 签名验证机制

### 1. 时间戳验证

为防止重放攻击，过滤器会验证请求时间戳的有效性：

- **标准方法**：时间戳在当前时间的 ±300 秒（5分钟）内有效
- **WX方法**：时间戳在当前时间的 ±60 秒内有效

### 2. 标准 HMAC-SHA256 签名验证

签名内容构建规则：

```
签名内容 = appId + timestamp + nonce + 请求内容
签名算法 = HMAC-SHA256
签名编码 = Base64
```

**请求内容获取规则**：
- POST/PUT 请求：使用请求体内容
- GET/DELETE 请求：使用查询参数（按参数名排序后拼接）

### 3. 微信小程序简化签名验证

签名内容构建规则：

```
签名内容 = appId + timestamp + nonce + method + url + data
```

其中：
- method：大写请求方法（GET/POST/PUT/DELETE）
- url：请求路径（不包含上下文路径）
- data：POST/PUT 请求体内容，或 GET/DELETE 的排序后查询参数

签名算法使用自定义哈希：

```java
int hash = 0;
for (char c : content.toCharArray()) {
    hash = 31 * hash + c;
}
expectedSignature = Integer.toHexString(hash);
```

## 认证信息构建

签名验证成功后，过滤器会构建包含以下信息的认证 Token：

1. **基础角色**：添加 `ROLE_APP` 角色
2. **应用权限**：从数据库读取 Appid 的 permissions 字段，按逗号分隔提取权限
3. **CustomUserDetails**：包含 appId（作为username）、租户ID等

## 数据库依赖

过滤器依赖 `AppidService` 查询应用信息，需要确保数据库中存在 `appid` 表，且包含以下字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| app_id | varchar | 应用唯一标识 |
| secret | varchar | 应用密钥 |
| permissions | varchar | 权限列表，逗号分隔 |
| status | boolean | 应用状态 |

## 使用示例

### Java 签名生成示例

```java
public class SignatureUtil {

    public static String generateSignature(String appId, String timestamp,
            String nonce, String secret, String method, String url, String body) {
        StringBuilder content = new StringBuilder();
        content.append(appId).append(timestamp).append(nonce)
               .append(method).append(url);
        if (body != null && !body.isEmpty()) {
            content.append(body);
        }

        String expectedContent = content.toString() + secret;
        int hash = 0;
        for (char c : expectedContent.toCharArray()) {
            hash = 31 * hash + c;
        }
        return Integer.toHexString(hash);
    }

    public static String generateHMACSHA256Signature(String appId,
            String timestamp, String nonce, String content, String secret) {
        String data = appId + timestamp + nonce + content;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secret.getBytes("UTF-8"), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] bytes = mac.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

### HTTP 请求示例

**微信小程序方式**：
```http
POST /api/resource HTTP/1.1
Host: example.com
X-App-Id: your_app_id
X-Signature: abc123signature
X-Timestamp: 1713792000
X-Nonce: random_string_123
X-Sign-Method: WX
Content-Type: application/json

{"name": "value"}
```

**标准HMAC方式**：
```http
GET /api/resource?id=1 HTTP/1.1
Host: example.com
X-App-Id: your_app_id
X-Signature: base64_signature==
X-Timestamp: 1713792000
X-Nonce: random_string_123
X-Sign-Method: HMAC-SHA256
```

## 安全注意事项

1. **Nonce 唯一性**：每次请求应使用不同的随机字符串，建议使用 UUID
2. **时间同步**：确保服务器时间与客户端时间误差在可接受范围内
3. **密钥安全**：应用密钥应在安全环境中存储，不要硬编码或暴露在前端
4. **HTTPS**：生产环境务必使用 HTTPS 协议传输请求
5. **日志脱敏**：日志中避免记录完整签名和密钥信息

## 错误处理

签名验证失败时，过滤器不会直接返回错误响应，而是记录日志并继续执行过滤器链，由后续的 Spring Security 授权流程处理未认证的请求。

## 相关代码

- 过滤器实现：`io.github.zmxckj.flexiadmin.security.AppIdAuthenticationFilter`
- 认证用户详情：`io.github.zmxckj.flexiadmin.security.CustomUserDetails`
- 应用服务：`io.github.zmxckj.flexiadmin.service.AppidService`
- 应用实体：`io.github.zmxckj.flexiadmin.entity.Appid`
