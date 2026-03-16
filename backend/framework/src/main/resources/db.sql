-- 创建数据库
CREATE DATABASE IF NOT EXISTS flexi_admin CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE flexi_admin;

-- 删除现有表
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_user_dept;
DROP TABLE IF EXISTS sys_dept;
DROP TABLE IF EXISTS sys_role_menu;
DROP TABLE IF EXISTS sys_menu;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_operation_log;
DROP TABLE IF EXISTS sys_login_log;
DROP TABLE IF EXISTS sys_config;
DROP TABLE IF EXISTS sys_dict;
DROP TABLE IF EXISTS sys_task;
DROP TABLE IF EXISTS sys_image;

-- 创建用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    status BOOLEAN DEFAULT TRUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建部门表
CREATE TABLE IF NOT EXISTS sys_dept (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    leader VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    status BOOLEAN DEFAULT TRUE,
    order_num INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建用户部门关联表
CREATE TABLE IF NOT EXISTS sys_user_dept (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    dept_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_dept (user_id, dept_id)
);

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id)
);

-- 创建角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    status BOOLEAN DEFAULT TRUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    path VARCHAR(100),
    component VARCHAR(100),
    parent_id BIGINT DEFAULT 0,
    icon VARCHAR(50),
    code VARCHAR(50) COMMENT '权限编码',
    type VARCHAR(20) DEFAULT 'menu' COMMENT '类型：menu(菜单)、operation(操作)',
    status BOOLEAN DEFAULT TRUE COMMENT '状态：true(正常)、false(禁用)',
    order_num INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建角色菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_menu (role_id, menu_id)
);

-- 创建操作日志表
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    operation VARCHAR(200),
    ip VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建登录日志表
CREATE TABLE IF NOT EXISTS sys_login_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    ip VARCHAR(50),
    status BOOLEAN,
    message VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建系统配置表
CREATE TABLE IF NOT EXISTS sys_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(50) NOT NULL UNIQUE,
    value VARCHAR(500),
    description VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建字典表
CREATE TABLE IF NOT EXISTS sys_dict (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL,
    value VARCHAR(200),
    order_num INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dict_type_code (type, code)
);

-- 创建定时任务表
CREATE TABLE IF NOT EXISTS sys_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    cron_expression VARCHAR(50) NOT NULL,
    class_name VARCHAR(200) NOT NULL,
    status BOOLEAN DEFAULT FALSE,
    description VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建图片管理表
CREATE TABLE IF NOT EXISTS sys_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 插入初始数据
-- 插入角色
INSERT INTO sys_role (id, name, description) VALUES (1, 'admin', '管理员');
INSERT INTO sys_role (id, name, description) VALUES (2, 'log_admin', '日志管理员');

-- 插入用户
INSERT INTO sys_user (id, username, password, nickname, status) VALUES (1, 'admin', '$2a$10$ct6Dw92R4r1aEVQ4oOAmRuv/DNOC7v906EPJ5VCYXq9.JYZo/Eu1O', '管理员', true);
INSERT INTO sys_user (id, username, password, nickname, status) VALUES (2, 'test_user', '$2a$10$ct6Dw92R4r1aEVQ4oOAmRuv/DNOC7v906EPJ5VCYXq9.JYZo/Eu1O', '测试用户', true);

-- 插入菜单和操作数据
INSERT INTO sys_menu (id, name, path, component, parent_id, icon, code, type, status, order_num) VALUES
-- 菜单
(1, '系统管理', '/system', '', 0, 'Setting', NULL, 'menu', true, 1),
(2, '用户管理', '/system/user', 'user/Index', 1, 'User', NULL, 'menu', true, 2),
(3, '角色管理', '/system/role', 'role/Index', 1, 'User', NULL, 'menu', true, 3),
(4, '菜单管理', '/system/menu', 'menu/Index', 1, 'Menu', NULL, 'menu', true, 4),
(5, '部门管理', '/system/dept', 'dept/Index', 1, 'OfficeBuilding', NULL, 'menu', true, 5),
(8, '系统配置', '/system/config', 'config/Index', 1, 'Setting', NULL, 'menu', true, 8),
(9, '日志管理', '/log', '', 0, 'Document', NULL, 'menu', true, 9),
(10, '操作日志', '/log/operation', 'log/OperationLog', 9, 'Document', NULL, 'menu', true, 10),
(11, '登录日志', '/log/login', 'log/LoginLog', 9, 'Document', NULL, 'menu', true, 11),
(12, '字典管理', '/dict', 'dict/Index', 0, 'List', NULL, 'menu', true, 12),
(13, '定时任务', '/task', 'task/Index', 0, 'Timer', NULL, 'menu', true, 13),
(14, '图片管理', '/image', 'image/Index', 0, 'Picture', NULL, 'menu', true, 14),
-- 操作
(15, '用户列表', NULL, NULL, 2, NULL, 'user:list', 'operation', true, 15),
(16, '用户添加', NULL, NULL, 2, NULL, 'user:add', 'operation', true, 16),
(17, '用户更新', NULL, NULL, 2, NULL, 'user:update', 'operation', true, 17),
(18, '用户删除', NULL, NULL, 2, NULL, 'user:delete', 'operation', true, 18),
(19, '角色列表', NULL, NULL, 3, NULL, 'role:list', 'operation', true, 19),
(20, '角色添加', NULL, NULL, 3, NULL, 'role:add', 'operation', true, 20),
(21, '角色更新', NULL, NULL, 3, NULL, 'role:update', 'operation', true, 21),
(22, '角色删除', NULL, NULL, 3, NULL, 'role:delete', 'operation', true, 22),
(23, '菜单列表', NULL, NULL, 4, NULL, 'menu:list', 'operation', true, 23),
(24, '菜单添加', NULL, NULL, 4, NULL, 'menu:add', 'operation', true, 24),
(25, '菜单更新', NULL, NULL, 4, NULL, 'menu:update', 'operation', true, 25),
(26, '菜单删除', NULL, NULL, 4, NULL, 'menu:delete', 'operation', true, 26),
(27, '部门列表', NULL, NULL, 5, NULL, 'dept:list', 'operation', true, 27),
(28, '部门添加', NULL, NULL, 5, NULL, 'dept:add', 'operation', true, 28),
(29, '部门更新', NULL, NULL, 5, NULL, 'dept:update', 'operation', true, 29),
(30, '部门删除', NULL, NULL, 5, NULL, 'dept:delete', 'operation', true, 30),
(31, '日志列表', NULL, NULL, 9, NULL, 'log:list', 'operation', true, 31),
(32, '图片列表', NULL, NULL, 14, NULL, 'image:list', 'operation', true, 32),
(33, '图片上传', NULL, NULL, 14, NULL, 'image:upload', 'operation', true, 33),
(34, '图片删除', NULL, NULL, 14, NULL, 'image:delete', 'operation', true, 34);

-- 插入角色菜单关联
-- 为 admin 角色分配所有菜单和操作权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14),
(1, 15), (1, 16), (1, 17), (1, 18), (1, 19), (1, 20), (1, 21), (1, 22), (1, 23), (1, 24), (1, 25), (1, 26), (1, 27), (1, 28), (1, 29), (1, 30), (1, 31), (1, 32), (1, 33), (1, 34);

-- 为日志管理员角色分配权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2, 9), (2, 10), (2, 11), (2, 31);

-- 插入用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1), -- admin用户关联admin角色
(2, 2); -- test_user用户关联log_admin角色

-- 插入系统配置
INSERT INTO sys_config (config_key, value, description) VALUES
('system.name', 'Flexi Admin', '系统名称'),
('system.version', '1.0.0', '系统版本'),
('system.enabled_modules', 'user,role,menu,log,config,dict,task,image', '启用的模块'),
('system.default_home', '/system/user', '默认首页路径'),
('system.image_base_url', '/api/images/', '图片基础URL');
