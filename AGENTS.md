# 任何时候，都要遵守以下规则

- 当有新的代码改动时，不要自动启动项目/重启项目，我会手动管理项目的启动或重启。
- 对于后端程序，尽量用mybatis-plus提供的方法来操作数据库，而不是编写xml文件的方式，优先使用简单查询，而不是join
- 自动配置文件的变更 ：在 Spring Boot 2.7+ 版本中，自动配置文件的位置确实从 META-INF/spring.factories 改为了 META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 。这是 Spring Boot 官方推荐的新方式，用于简化自动配置的声明。

# 以下文档中包含了框架的用法说明，

- 项目的整体需求文档： /START.md
- 前端动态菜单的实现细节：/docs/DYNAMIC\_MENU\_IMPLEMENTATION.md
- 前端双列侧边栏的实现细节：/docs/SIDEBAR\_LAYOUT\_IMPLEMENTATION.md
- 登录流程的实现细节：/docs/LOGIN\_IMPLEMENTATION.md
- 权限系统的实现细节：/docs/PERMISSION\_IMPLEMENTATION.md
- appid鉴权规则：/docs/APPID\_AUTH.md
- excel导入导出机制：/docs/EXCEL\_IMPLEMENTATION.md
- framework的数据库设计：/backend/framework/src/main/resources/db.sql

