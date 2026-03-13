# 权限控制实现方案

## 1. 整体架构

本项目采用基于角色的权限控制（RBAC）模型，结合 Spring Security 和 MyBatis-Plus 实现细粒度的权限管理。

### 核心组件

- **权限实体**：`Permission` 实体类，用于存储权限信息
- **角色权限关联**：`RolePermission` 实体类，用于存储角色与权限的关联关系
- **权限验证**：基于 Spring Security 的 AOP 切面实现方法级权限验证
- **权限缓存**：使用 Redis 缓存用户权限信息，提高验证效率

## 2. 数据库设计

### 权限表 (`sys_permission`)

| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| `id` | `BIGINT` | 权限ID |
| `name` | `VARCHAR(255)` | 权限名称 |
| `code` | `VARCHAR(255)` | 权限代码 |
| `description` | `VARCHAR(255)` | 权限描述 |
| `status` | `BOOLEAN` | 状态 |
| `create_time` | `DATETIME` | 创建时间 |
| `update_time` | `DATETIME` | 更新时间 |

### 角色权限关联表 (`sys_role_permission`)

| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| `id` | `BIGINT` | 主键ID |
| `role_id` | `BIGINT` | 角色ID |
| `permission_id` | `BIGINT` | 权限ID |
| `create_time` | `DATETIME` | 创建时间 |

## 3. 核心代码实现

### 3.1 实体类

- **Permission 实体**：`/backend/framework/src/main/java/com/flexi/framework/entity/Permission.java`
- **RolePermission 实体**：`/backend/framework/src/main/java/com/flexi/framework/entity/RolePermission.java`

### 3.2 Mapper 接口

- **PermissionMapper**：`/backend/framework/src/main/java/com/flexi/framework/mapper/PermissionMapper.java`
  - 继承自 `BaseMapper<Permission>`，使用 MyBatis-Plus 提供的默认方法

- **RolePermissionMapper**：`/backend/framework/src/main/java/com/flexi/framework/mapper/RolePermissionMapper.java`
  - 继承自 `BaseMapper<RolePermission>`，使用 MyBatis-Plus 提供的默认方法

### 3.3 Service 层

- **PermissionService**：`/backend/framework/src/main/java/com/flexi/framework/service/PermissionService.java`
  - 定义权限相关的服务接口

- **PermissionServiceImpl**：`/backend/framework/src/main/java/com/flexi/framework/service/impl/PermissionServiceImpl.java`
  - 实现权限服务，使用 MyBatis-Plus 的 `selectList` 方法查询权限

- **RolePermissionService**：`/backend/framework/src/main/java/com/flexi/framework/service/RolePermissionService.java`
  - 定义角色权限关联相关的服务接口

- **RolePermissionServiceImpl**：`/backend/framework/src/main/java/com/flexi/framework/service/impl/RolePermissionServiceImpl.java`
  - 实现角色权限关联服务，使用 MyBatis-Plus 的 `selectList` 方法查询角色权限关联

### 3.4 权限验证

- **RequirePermission 注解**：`/backend/framework/src/main/java/com/flexi/framework/security/RequirePermission.java`
  - 自定义注解，用于标记需要权限验证的方法

- **PermissionAspect**：`/backend/framework/src/main/java/com/flexi/framework/security/PermissionAspect.java`
  - AOP 切面，用于拦截标记了 `@RequirePermission` 注解的方法，进行权限验证

### 3.5 配置类

- **SecurityConfig**：`/backend/framework/src/main/java/com/flexi/framework/security/SecurityConfig.java`
  - Spring Security 配置类，启用方法级安全

## 4. 权限验证流程

1. **用户登录**：用户登录成功后，系统会查询用户的角色和权限信息
2. **权限缓存**：将用户的权限信息缓存到 Redis 中，键为 `user:permissions:{userId}`
3. **请求处理**：当用户请求需要权限验证的接口时，`PermissionAspect` 会拦截请求
4. **权限检查**：`PermissionAspect` 会从 Redis 中获取用户的权限信息，检查用户是否拥有请求所需的权限
5. **权限验证**：如果用户拥有所需权限，则继续处理请求；否则，返回权限不足的错误

## 5. 使用方法

### 5.1 标记需要权限验证的方法

在需要权限验证的方法上添加 `@RequirePermission` 注解，指定所需的权限代码：

```java
@RequirePermission("user:list")
@GetMapping("/list")
public Result list(@RequestParam Integer page, @RequestParam Integer pageSize) {
    // 方法实现
}
```

### 5.2 配置用户权限

通过页面配置用户的角色，然后为角色分配权限。权限代码需要与 `@RequirePermission` 注解中指定的权限代码对应。

### 5.3 权限验证流程

1. 用户登录后，系统会自动缓存用户的权限信息
2. 当用户访问标记了 `@RequirePermission` 注解的接口时，系统会自动验证用户是否拥有所需的权限
3. 如果用户没有所需的权限，系统会返回 403 错误

## 6. 注意事项

1. **权限代码设计**：权限代码应该具有一定的层级结构，例如 `user:list`、`user:add`、`user:edit` 等
2. **权限缓存**：权限信息缓存到 Redis 中，当用户权限发生变化时，需要清除缓存
3. **权限验证**：权限验证是基于方法级的，需要在每个需要权限验证的方法上添加 `@RequirePermission` 注解
4. **数据库操作**：所有的数据库操作都使用 MyBatis-Plus 的方法，没有使用 XML 配置

## 7. 代码优化建议

1. **权限缓存优化**：可以考虑使用 Redis 的过期时间，自动清除过期的权限缓存
2. **权限验证优化**：可以考虑使用 Redis 的 BitMap 来存储用户权限，提高权限验证的效率
3. **权限管理优化**：可以考虑添加权限继承机制，让角色可以继承其他角色的权限

## 8. 总结

本项目的权限控制实现采用了基于角色的权限控制（RBAC）模型，结合 Spring Security 和 MyBatis-Plus 实现了细粒度的权限管理。通过 AOP 切面和自定义注解，实现了方法级的权限验证，提高了系统的安全性和可维护性。同时，使用 Redis 缓存用户权限信息，提高了权限验证的效率。