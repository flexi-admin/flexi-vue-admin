# 动态菜单实现细节

本文档描述了 Flexi Admin 项目中动态菜单的实现细节，包括前端请求菜单数据、后端组织数据、前端动态组织路由等过程。

## 1. 后端实现

### 1.1 菜单实体类

菜单实体类位于 `backend/framework/src/main/java/com/flexi/framework/entity/Menu.java`，包含以下字段：

- id：菜单 ID
- name：菜单名称
- path：菜单路径
- component：组件路径
- parentId：父菜单 ID
- icon：菜单图标
- orderNum：排序号
- status：菜单状态
- createTime：创建时间
- updateTime：更新时间
- children：子菜单列表（用于构建菜单树）

### 1.2 菜单服务

菜单服务接口位于 `backend/framework/src/main/java/com/flexi/framework/service/MenuService.java`，主要方法：

- `findAll()`：获取所有菜单
- `findByRoleId(Long roleId)`：根据角色 ID 获取菜单
- `getMenuTree()`：获取菜单树

菜单服务实现类位于 `backend/framework/src/main/java/com/flexi/framework/service/impl/MenuServiceImpl.java`，主要功能：

- 实现菜单查询和菜单树构建逻辑
- 使用递归方式构建菜单树结构

### 1.3 菜单控制器

菜单控制器位于 `backend/framework/src/main/java/com/flexi/framework/controller/MenuController.java`，主要接口：

- `GET /menu/list`：获取菜单列表
- `GET /menu/page`：分页获取菜单
- `POST /menu`：添加菜单
- `PUT /menu`：更新菜单
- `DELETE /menu/{id}`：删除菜单
- `GET /menu/{id}`：获取菜单详情
- `GET /menu/role/{roleId}`：根据角色 ID 获取菜单
- `GET /menu/tree`：获取菜单树

## 2. 前端实现

### 2.1 路由配置

路由配置位于 `frontend/src/router/index.ts`，主要功能：

- 配置静态路由（登录页、首页）
- 实现动态路由添加功能 `addDynamicRoutes`
- 使用 Vite 的 `import.meta.glob` 功能预加载所有视图组件
- 实现路由守卫，确保用户登录状态和动态路由加载

### 2.2 菜单数据获取

菜单数据获取位于 `frontend/src/views/Home.vue`，主要功能：

- 从后端获取菜单数据：调用 `/menu/tree` 接口
- 转换后端数据结构为前端期望的结构
- 设置默认选中的一级菜单

### 2.3 菜单渲染

菜单渲染位于 `frontend/src/views/Home.vue`，主要功能：

- 渲染左侧一级菜单
- 点击一级菜单，显示右侧二级菜单
- 点击二级菜单，跳转到对应的路由
- 实现标签栏功能，支持标签切换和关闭

## 3. 实现流程

### 3.1 登录流程

1. 用户访问登录页面 `http://localhost:5173/login`
2. 用户输入用户名和密码，点击登录按钮
3. 前端调用 `/auth/login` 接口获取 token
4. 前端调用 `/auth/user` 接口获取用户信息
5. 前端将 token 和用户信息存储到本地存储
6. 前端跳转到根路径 `/`

### 3.2 动态菜单加载流程

1. 用户访问任何页面（除了登录页）
2. 路由守卫检查用户是否已登录
3. 如果未登录，重定向到登录页
4. 如果已登录但用户信息为空，获取用户信息
5. 检查是否已经添加了动态路由
6. 如果没有添加动态路由，调用 `/menu/tree` 接口获取菜单数据
7. 转换菜单数据格式，添加动态路由
8. 重新触发路由导航，确保新添加的路由能够被正确匹配
9. 正常跳转

### 3.3 菜单渲染流程

1. 前端获取菜单数据
2. 转换菜单数据格式为前端期望的结构
3. 渲染左侧一级菜单
4. 点击一级菜单，显示右侧二级菜单
5. 点击二级菜单，跳转到对应的路由

## 4. 技术要点

### 4.1 后端技术要点

- 使用 MyBatis-Plus 进行数据库操作
- 使用递归构建菜单树结构
- 提供 `/menu/tree` 接口返回菜单树数据

### 4.2 前端技术要点

- 使用 Vue 3 + TypeScript 构建前端应用
- 使用 Vue Router 进行路由管理
- 使用 Element Plus 进行 UI 渲染
- 使用 Vite 的 `import.meta.glob` 功能预加载所有视图组件
- 使用路由守卫确保用户登录状态和动态路由加载
- 使用 Flexbox 布局确保菜单高度覆盖整个窗口

## 5. 注意事项

- 菜单数据中的 `component` 字段需要与前端视图组件的路径对应
- 菜单数据中的 `path` 字段需要与前端路由的路径对应
- 前端需要确保在路由守卫中添加动态路由，否则直接访问动态路由路径时会出现白屏
- 前端需要确保菜单的高度能够覆盖整个窗口，否则会影响用户体验

## 6. 总结

通过以上实现，Flexi Admin 项目实现了动态菜单功能，包括：

- 后端提供菜单树数据
- 前端根据菜单数据动态添加路由
- 前端根据菜单数据渲染菜单
- 前端确保菜单高度覆盖整个窗口

这样，用户可以通过后端管理系统配置菜单，前端会自动根据配置的菜单数据生成对应的路由和菜单，实现了菜单的动态管理。