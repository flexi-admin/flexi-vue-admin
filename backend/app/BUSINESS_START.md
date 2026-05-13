在app这个项目下实现具体的业务逻辑
- app以backend/framework为基础，通过pom依赖引入framework项目中的代码
- 具体业务代码全部写在app项目中
- 业务相关的前端代码，放在frontend项目的views/business目录下
- 将具体业务数据库sql代码写在app项目的src/main/resources/business.sql，业务菜单和权限数据放在src/main/resources/menu.sql