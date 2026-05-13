CREATE TABLE IF NOT EXISTS code_analysis (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    namespace VARCHAR(255) DEFAULT NULL COMMENT '命名空间',
    class_name VARCHAR(255) DEFAULT NULL COMMENT '类名',
    method_name VARCHAR(255) DEFAULT NULL COMMENT '方法名',
    method_params VARCHAR(500) DEFAULT NULL COMMENT '方法参数',
    method_return VARCHAR(255) DEFAULT NULL COMMENT '方法返回值',
    method_desc TEXT COMMENT '方法说明',
    method_brief VARCHAR(500) DEFAULT NULL COMMENT '方法简述',
    is_db_interaction TINYINT(1) DEFAULT 0 COMMENT '是否是数据库交互：0-否，1-是',
    file VARCHAR(500) DEFAULT NULL COMMENT '所在文件',
    PRIMARY KEY (id),
    INDEX idx_method_name (method_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代码分析表';

-- 插入代码分析菜单（业务模块）
INSERT INTO sys_menu (id, name, path, component, parent_id, icon, code, type, status, is_show, order_num) VALUES
-- 业务管理父菜单
(100, '业务管理', '/business', '', 0, 'Briefcase', NULL, 'menu', true, true, 100),
-- 代码分析菜单
(101, '代码分析', '/business/code-analysis', 'business/code-analysis/Index', 100, 'Code', NULL, 'menu', true, true, 101);

-- 插入代码分析操作权限
INSERT INTO sys_menu (id, name, path, component, parent_id, icon, code, type, status, is_show, order_num) VALUES
(102, '代码分析列表', NULL, NULL, 101, NULL, 'codeAnalysis:list', 'operation', true, false, 102),
(103, '代码分析新增', NULL, NULL, 101, NULL, 'codeAnalysis:add', 'operation', true, false, 103),
(104, '代码分析编辑', NULL, NULL, 101, NULL, 'codeAnalysis:edit', 'operation', true, false, 104),
(105, '代码分析删除', NULL, NULL, 101, NULL, 'codeAnalysis:delete', 'operation', true, false, 105);

-- 为 admin 角色分配代码分析权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 100), (1, 101), (1, 102), (1, 103), (1, 104), (1, 105);