-- 资产类型表
CREATE TABLE IF NOT EXISTS `asset_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` VARCHAR(255) NOT NULL COMMENT '名称',
  `code` VARCHAR(50) NOT NULL COMMENT '编码',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父ID',
  `path` VARCHAR(500) COMMENT '路径',
  `level` INT NOT NULL DEFAULT 1 COMMENT '层级',
  `remark` VARCHAR(500) COMMENT '备注',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `create_time` BIGINT NOT NULL COMMENT '创建时间',
  `update_time` BIGINT NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产类型表';

-- 资产位置表
CREATE TABLE IF NOT EXISTS `asset_location` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` VARCHAR(255) NOT NULL COMMENT '名称',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父ID',
  `path` VARCHAR(500) COMMENT '路径',
  `level` INT NOT NULL DEFAULT 1 COMMENT '层级',
  `remark` VARCHAR(500) COMMENT '备注',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `beacon_mac` VARCHAR(100) COMMENT '信标mac',
  `beacon2_mac` VARCHAR(100) COMMENT '信标2mac',
  `ooomap_url` VARCHAR(500) COMMENT '园图url',
  `ooomap_appid` VARCHAR(100) COMMENT '园图appid',
  `x` DOUBLE COMMENT 'x坐标',
  `y` DOUBLE COMMENT 'y坐标',
  `z` DOUBLE COMMENT 'z坐标',
  `tenant_id` BIGINT COMMENT '租户ID',
  `create_time` BIGINT NOT NULL COMMENT '创建时间',
  `update_time` BIGINT NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_parent_id` (`parent_id`),
  INDEX `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产位置表';

-- 资产表
CREATE TABLE IF NOT EXISTS `asset` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` VARCHAR(255) NOT NULL COMMENT '名称',
  `code` VARCHAR(255) NOT NULL COMMENT '编码',
  `type_code` VARCHAR(50) NOT NULL COMMENT '类型编码',
  `custom_type_code` VARCHAR(50) COMMENT '自定义类型编码',
  `location_id` BIGINT NOT NULL COMMENT '位置ID',
  `specification` VARCHAR(500) COMMENT '规格',
  `model` VARCHAR(255) COMMENT '型号',
  `manufacturer` VARCHAR(255) COMMENT '制造商',
  `supplier_id` BIGINT COMMENT '供应商ID',
  `purchase_date` DATE COMMENT '购买日期',
  `warehouse_date` DATE COMMENT '入库日期',
  `enable_date` DATE COMMENT '启用日期',
  `price` DECIMAL(10,2) COMMENT '价格',
  `status` VARCHAR(50) COMMENT '状态',
  `remark` VARCHAR(500) COMMENT '备注',
  `is_deleted` INT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  `image` VARCHAR(500) COMMENT '图片',
  `label_type` VARCHAR(50) COMMENT '标签类型（普通标签、RFID标签）',
  `label_code` VARCHAR(255) COMMENT '标签编码',
  `admin_user_id` BIGINT COMMENT '管理员用户ID',
  `user_id` BIGINT COMMENT '使用人用户ID',
  `dept_id` BIGINT COMMENT '部门ID',
  `sn` VARCHAR(255) COMMENT 'SN码',
  `source` VARCHAR(50) COMMENT '资产来源（自产、采购）',
  `current_value` DECIMAL(10,2) COMMENT '当前价值',
  `accumulated_depreciation_value` DECIMAL(10,2) COMMENT '已计提折旧价值',
  `unit` VARCHAR(50) COMMENT '计量单位',
  `creator_id` BIGINT COMMENT '创建人',
  `updater_id` BIGINT COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  UNIQUE KEY `uk_label_code` (`label_code`),
  INDEX `idx_type_code` (`type_code`),
  INDEX `idx_location_id` (`location_id`),
  INDEX `idx_admin_user_id` (`admin_user_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产表';

-- 供应商表
CREATE TABLE IF NOT EXISTS `asset_supplier` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` VARCHAR(255) NOT NULL COMMENT '名称',
  `contact_person` VARCHAR(100) COMMENT '联系人',
  `contact_phone` VARCHAR(50) COMMENT '联系电话',
  `contact_address` VARCHAR(500) COMMENT '联系地址',
  `remark` VARCHAR(500) COMMENT '备注',
  `is_deleted` INT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `create_time` BIGINT NOT NULL COMMENT '创建时间',
  `update_time` BIGINT NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 资产盘点表
CREATE TABLE IF NOT EXISTS `asset_inventory` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `inventory_code` VARCHAR(255) NOT NULL COMMENT '盘点单号',
  `inventory_name` VARCHAR(255) NOT NULL COMMENT '盘点名称',
  `inventory_type` VARCHAR(50) NOT NULL COMMENT '盘点类型（全部盘点、按部门盘点、按分类盘点）',
  `inventory_depts` VARCHAR(500) COMMENT '盘点部门IDs，逗号分隔',
  `inventory_categories` VARCHAR(500) COMMENT '盘点分类IDs，逗号分隔',
  `start_time` BIGINT NOT NULL COMMENT '开始时间',
  `end_time` BIGINT COMMENT '结束时间',
  `status` VARCHAR(50) NOT NULL COMMENT '状态（进行中、已完成、已取消）',
  `creator_id` BIGINT COMMENT '创建人ID',
  `create_time` BIGINT NOT NULL COMMENT '创建时间',
  `update_time` BIGINT NOT NULL COMMENT '更新时间',
  `remark` VARCHAR(500) COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inventory_code` (`inventory_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产盘点表';

-- 资产盘点明细表
CREATE TABLE IF NOT EXISTS `asset_inventory_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `inventory_id` BIGINT NOT NULL COMMENT '盘点单ID',
  `asset_id` BIGINT NOT NULL COMMENT '资产ID',
  `expected_quantity` INT NOT NULL DEFAULT 1 COMMENT '预期数量',
  `actual_quantity` INT NOT NULL DEFAULT 1 COMMENT '实际数量',
  `status` VARCHAR(50) NOT NULL COMMENT '状态（正常、盘盈、盘亏）',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` BIGINT NOT NULL COMMENT '创建时间',
  `update_time` BIGINT NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_inventory_id` (`inventory_id`),
  INDEX `idx_asset_id` (`asset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产盘点明细表';

create table IF NOT EXISTS `asset_type_old`
(
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `code`     int         null comment '代码',
    `name`     varchar(50) null comment '名称',
    `new_code` varchar(50) null comment '2022版代码',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '2010版资产类型表含2022对照';


-- 插入字典数据
-- 资产状态字典数据
INSERT INTO sys_dict (type, code, value, order_num, create_time, update_time) VALUES
('asset_status', 'idle', '闲置', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_status', 'in_use', '使用中', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_status', 'repairing', '维修中', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_status', 'scrapped', '已报废', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 资产来源字典数据
INSERT INTO sys_dict (type, code, value, order_num, create_time, update_time) VALUES
('asset_source', 'self_produced', '自产', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_source', 'purchased', '采购', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 计量单位字典数据
INSERT INTO sys_dict (type, code, value, order_num, create_time, update_time) VALUES
('asset_unit', 'unit', '台', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_unit', 'piece', '个', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_unit', 'set', '套', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_unit', 'sheet', '张', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 资产盘点状态字典数据
INSERT INTO sys_dict (type, code, value, order_num, create_time, update_time) VALUES
('inventory_status', 'pending', '未开始', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inventory_status', 'in_progress', '进行中', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inventory_status', 'completed', '已完成', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inventory_status', 'cancelled', '已取消', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 资产盘点明细状态字典数据
INSERT INTO sys_dict (type, code, value, order_num, create_time, update_time) VALUES
('inventory_detail_status', 'pending', '未盘点', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inventory_detail_status', 'normal', '正常', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inventory_detail_status', 'overage', '盘盈', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inventory_detail_status', 'shortage', '盘亏', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- 插入菜单和权限
-- 资产管理主菜单
INSERT INTO sys_menu (id, name, path, component, parent_id, icon, code, type, status, order_num) VALUES
(10000, '资产管理', '/asset', '', 0, 'Briefcase', NULL, 'menu', true, 15),
-- 资产类型管理
(10001, '资产类型管理', '/asset/type', 'business/asset-type/Index', 10000, 'List', NULL, 'menu', true, 16),
-- 资产位置管理
(10002, '资产位置管理', '/asset/location', 'business/asset-location/Index', 10000, 'Location', NULL, 'menu', true, 17),
-- 资产管理
(10003, '资产管理', '/asset/list', 'business/asset/Index', 10000, 'Inventory', NULL, 'menu', true, 19),
-- 资产盘点管理
(10004, '资产盘点', '/asset/inventory', 'business/asset-inventory/Index', 10000, 'DataAnalysis', NULL, 'menu', true, 20),
-- 资产定位
(10005, '资产定位', '/asset/location-tracking', 'business/asset/LocationTracking', 10000, 'Location', NULL, 'menu', true, 21),
-- 资产报废
(10008, '资产报废', '/asset/scrap', 'business/asset/Scrap', 10000, 'Delete', NULL, 'menu', true, 26),
-- 资产处置
(10009, '资产处置', '/asset/dispose', 'business/asset/Dispose', 10000, 'Delete', NULL, 'menu', true, 27),
-- 资产变更
(10010, '资产变更', '/asset/change', 'business/asset/Change', 10000, 'Edit', NULL, 'menu', true, 28),
-- 报废列表
(10011, '报废列表', '/asset/scrap-list', 'business/asset/ScrapList', 10000, 'List', NULL, 'menu', true, 29),
-- 折旧设置
(10012, '折旧设置', '/asset/depreciation', 'business/asset/Depreciation', 10000, 'Settings', NULL, 'menu', true, 30),
-- 采购管理
(10100, '采购管理', '/purchase', '', 0, 'ShoppingCart', NULL, 'menu', true, 20),
-- 采购管理二级菜单
-- 供应商管理
(10104, '供应商管理', '/purchase/supplier', 'business/supplier/Index', 10100, 'User', NULL, 'menu', true, 4),
-- 资产统计
(10300, '资产统计', '/asset-statistics', '', 0, 'PieChart', NULL, 'menu', true, 22),
-- 分类统计
(10301, '分类统计', '/asset-statistics/category', 'business/asset-statistics/Category', 10300, 'Layers', NULL, 'menu', true, 1),
-- 位置统计
(10302, '位置统计', '/asset-statistics/location', 'business/asset-statistics/Location', 10300, 'Location', NULL, 'menu', true, 2),
-- 部门资产统计
(10303, '部门资产统计', '/asset-statistics/dept', 'business/asset-statistics/Dept', 10300, 'Team', NULL, 'menu', true, 3),
-- 员工资产统计
(10304, '个人资产统计', '/asset-statistics/employee', 'business/asset-statistics/Employee', 10300, 'User', NULL, 'menu', true, 4),
-- 数据大屏
(10600, '数据大屏', '/dashboard', '', 0, 'DataAnalysis', NULL, 'menu', true, 25),
-- 个人中心主菜单
(10700, '个人中心', '/personal', '', 0, 'User', NULL, 'menu', true, 26),
-- 我的资产
(10701, '我的资产', '/personal/asset/my', 'business/asset/MyAsset', 10700, 'User', NULL, 'menu', true, 26),

-- 操作权限
-- 资产类型操作
INSERT INTO sys_menu (id, name, path, component, parent_id, icon, code, type, status, order_num) VALUES
(1000101, '资产类型列表', NULL, NULL, 10001, NULL, 'asset:type:list', 'operation', true, 1),
(1000102, '资产类型添加', NULL, NULL, 10001, NULL, 'asset:type:add', 'operation', true, 2),
(1000103, '资产类型编辑', NULL, NULL, 10001, NULL, 'asset:type:edit', 'operation', true, 3),
(1000104, '资产类型删除', NULL, NULL, 10001, NULL, 'asset:type:delete', 'operation', true, 4),
-- 资产位置操作
(1000201, '资产位置列表', NULL, NULL, 10002, NULL, 'asset:location:list', 'operation', true, 1),
(1000202, '资产位置添加', NULL, NULL, 10002, NULL, 'asset:location:add', 'operation', true, 2),
(1000203, '资产位置编辑', NULL, NULL, 10002, NULL, 'asset:location:edit', 'operation', true, 3),
(1000204, '资产位置删除', NULL, NULL, 10002, NULL, 'asset:location:delete', 'operation', true, 4),
-- 资产操作
(1000301, '资产列表', NULL, NULL, 10003, NULL, 'asset:list', 'operation', true, 1),
(1000302, '资产查询', NULL, NULL, 10003, NULL, 'asset:query', 'operation', true, 2),
(1000303, '资产添加', NULL, NULL, 10003, NULL, 'asset:add', 'operation', true, 3),
(1000304, '资产编辑', NULL, NULL, 10003, NULL, 'asset:edit', 'operation', true, 4),
(1000305, '资产删除', NULL, NULL, 10003, NULL, 'asset:delete', 'operation', true, 5),
(1000306, '资产标签打印', NULL, NULL, 10003, NULL, 'asset:print', 'operation', true, 6),
(1000307, '资产统计', NULL, NULL, 10003, NULL, 'asset:statistics', 'operation', true, 7),
-- 资产盘点操作权限
(1000401, '资产盘点列表', NULL, NULL, 10004, NULL, 'asset:inventory:list', 'operation', true, 1),
(1000402, '资产盘点添加', NULL, NULL, 10004, NULL, 'asset:inventory:add', 'operation', true, 2),
(1000403, '资产盘点编辑', NULL, NULL, 10004, NULL, 'asset:inventory:edit', 'operation', true, 3),
(1000404, '资产盘点删除', NULL, NULL, 10004, NULL, 'asset:inventory:delete', 'operation', true, 4),
(1000405, '资产盘点下发', NULL, NULL, 10004, NULL, 'asset:inventory:issue', 'operation', true, 5),
(1000406, '资产盘点统计', NULL, NULL, 10004, NULL, 'asset:inventory:statistics', 'operation', true, 6),
(1000407, '资产盘点查询', NULL, NULL, 10004, NULL, 'asset:inventory:query', 'operation', true, 7),
-- 资产定位操作权限
(1000501, '资产定位查看', NULL, NULL, 10005, NULL, 'asset:location:tracking:view', 'operation', true, 1),
(1000502, '资产定位查询', NULL, NULL, 10005, NULL, 'asset:location:tracking:query', 'operation', true, 2),
-- 供应商操作权限
(1010401, '供应商列表', NULL, NULL, 10104, NULL, 'asset:supplier:list', 'operation', true, 1),
(1010402, '供应商添加', NULL, NULL, 10104, NULL, 'asset:supplier:add', 'operation', true, 2),
(1010403, '供应商编辑', NULL, NULL, 10104, NULL, 'asset:supplier:edit', 'operation', true, 3),
(1010404, '供应商删除', NULL, NULL, 10104, NULL, 'asset:supplier:delete', 'operation', true, 4),
-- 分类统计操作
(1030101, '分类统计查看', NULL, NULL, 10301, NULL, 'asset:statistics:category:view', 'operation', true, 1),
-- 位置统计操作
(1030201, '位置统计查看', NULL, NULL, 10302, NULL, 'asset:statistics:location:view', 'operation', true, 1),
-- 部门资产统计操作
(1030301, '部门资产统计查看', NULL, NULL, 10303, NULL, 'asset:statistics:dept:view', 'operation', true, 1),
-- 员工资产统计操作
(1030401, '个人资产统计查看', NULL, NULL, 10304, NULL, 'asset:statistics:employee:view', 'operation', true, 1),
-- 我的资产操作
(1070101, '我的资产列表', NULL, NULL, 10701, NULL, 'asset:my:list', 'operation', true, 1),

-- 为admin角色分配权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
-- 资产管理权限
(1, 10000), 
(1, 10001), (1, 1000101), (1, 1000102), (1, 1000103), (1, 1000104),
(1, 10002), (1, 1000201), (1, 1000202), (1, 1000203), (1, 1000204),
(1, 10003), (1, 1000301), (1, 1000302), (1, 1000303), (1, 1000304), (1, 1000305), (1, 1000306), (1, 1000307),
(1, 10004), (1, 1000401), (1, 1000402), (1, 1000403), (1, 1000404), (1, 1000405), (1, 1000406), (1, 1000407),
(1, 10005), (1, 1000501), (1, 1000502),
(1, 10008),
(1, 10009),
(1, 10010),
(1, 10011),
(1, 10012),
-- 采购管理权限
(1, 10100), 
(1, 10104), (1, 1010401), (1, 1010402), (1, 1010403), (1, 1010404),
-- 资产统计权限
(1, 10300), 
(1, 10301), (1, 1030101), 
(1, 10302), (1, 1030201), 
(1, 10303), (1, 1030301), 
(1, 10304), (1, 1030401),
-- 数据大屏权限
(1, 10600),
-- 个人中心权限
(1, 10700), 
(1, 10701), (1, 1070101);

-- 插入系统配置
INSERT INTO sys_config (config_key, value, description, create_time, update_time) VALUES
('system.print_service_url', 'http://127.0.0.1:8000/api', '打印服务URL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

