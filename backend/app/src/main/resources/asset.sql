-- 资产类型表
CREATE TABLE IF NOT EXISTS `asset_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` VARCHAR(255) NOT NULL COMMENT '名称',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父ID',
  `path` VARCHAR(500) COMMENT '路径',
  `level` INT NOT NULL DEFAULT 1 COMMENT '层级',
  `remark` VARCHAR(500) COMMENT '备注',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `create_time` BIGINT NOT NULL COMMENT '创建时间',
  `update_time` BIGINT NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
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
  `create_time` BIGINT NOT NULL COMMENT '创建时间',
  `update_time` BIGINT NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产位置表';

-- 资产表
CREATE TABLE IF NOT EXISTS `asset` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` VARCHAR(255) NOT NULL COMMENT '名称',
  `code` VARCHAR(255) NOT NULL COMMENT '编码',
  `type_id` BIGINT NOT NULL COMMENT '类型ID',
  `location_id` BIGINT NOT NULL COMMENT '位置ID',
  `specification` VARCHAR(500) COMMENT '规格',
  `model` VARCHAR(255) COMMENT '型号',
  `manufacturer` VARCHAR(255) COMMENT '制造商',
  `supplier_id` BIGINT COMMENT '供应商ID',
  `purchase_date` VARCHAR(20) COMMENT '购买日期',
  `price` DECIMAL(10,2) COMMENT '价格',
  `status` VARCHAR(50) COMMENT '状态',
  `remark` VARCHAR(500) COMMENT '备注',
  `is_deleted` INT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `create_time` BIGINT NOT NULL COMMENT '创建时间',
  `update_time` BIGINT NOT NULL COMMENT '更新时间',
  `image` VARCHAR(500) COMMENT '图片',
  `label_type` VARCHAR(50) COMMENT '标签类型（普通标签、RFID标签）',
  `label_code` VARCHAR(255) COMMENT '标签编码',
  `admin_user_id` BIGINT COMMENT '管理员用户ID',
  `user_id` BIGINT COMMENT '使用人用户ID',
  `dept_id` BIGINT COMMENT '部门ID',
  `sn` VARCHAR(255) COMMENT 'SN码',
  `source` VARCHAR(50) COMMENT '资产来源（自产、采购）',
  `current_value` DECIMAL(10,2) COMMENT '当前价值',
  `unit` VARCHAR(50) COMMENT '计量单位',
  `creator_id` BIGINT COMMENT '创建人',
  `updater_id` BIGINT COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  UNIQUE KEY `uk_label_code` (`label_code`),
  INDEX `idx_type_id` (`type_id`),
  INDEX `idx_location_id` (`location_id`),
  INDEX `idx_admin_user_id` (`admin_user_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产表';

-- 插入资产管理菜单和权限
-- 插入菜单
INSERT INTO sys_menu (id, name, path, component, parent_id, icon, code, type, status, order_num) VALUES
-- 资产管理主菜单
(35, '资产管理', '/asset', '', 0, 'Briefcase', NULL, 'menu', true, 15),
-- 资产类型管理
(36, '资产类型管理', '/asset/type', 'business/asset-type/Index', 35, 'List', NULL, 'menu', true, 16),
-- 资产位置管理
(37, '资产位置管理', '/asset/location', 'business/asset-location/Index', 35, 'Location', NULL, 'menu', true, 17),
-- 资产管理
(38, '资产列表', '/asset/list', 'business/asset/Index', 35, 'Inventory', NULL, 'menu', true, 18),
-- 操作权限
-- 资产类型操作
(39, '资产类型列表', NULL, NULL, 36, NULL, 'asset:type:list', 'operation', true, 19),
(40, '资产类型添加', NULL, NULL, 36, NULL, 'asset:type:add', 'operation', true, 20),
(41, '资产类型编辑', NULL, NULL, 36, NULL, 'asset:type:edit', 'operation', true, 21),
(42, '资产类型删除', NULL, NULL, 36, NULL, 'asset:type:delete', 'operation', true, 22),
-- 资产位置操作
(43, '资产位置列表', NULL, NULL, 37, NULL, 'asset:location:list', 'operation', true, 23),
(44, '资产位置添加', NULL, NULL, 37, NULL, 'asset:location:add', 'operation', true, 24),
(45, '资产位置编辑', NULL, NULL, 37, NULL, 'asset:location:edit', 'operation', true, 25),
(46, '资产位置删除', NULL, NULL, 37, NULL, 'asset:location:delete', 'operation', true, 26),
-- 资产操作
(47, '资产列表', NULL, NULL, 38, NULL, 'asset:list', 'operation', true, 27),
(48, '资产添加', NULL, NULL, 38, NULL, 'asset:add', 'operation', true, 28),
(49, '资产编辑', NULL, NULL, 38, NULL, 'asset:edit', 'operation', true, 29),
(50, '资产删除', NULL, NULL, 38, NULL, 'asset:delete', 'operation', true, 30);

-- 为admin角色分配资产管理权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 35), (1, 36), (1, 37), (1, 38),
(1, 39), (1, 40), (1, 41), (1, 42),
(1, 43), (1, 44), (1, 45), (1, 46),
(1, 47), (1, 48), (1, 49), (1, 50);

-- 插入资产类型示例数据
-- 插入父节点
INSERT INTO asset_type (id, name, parent_id, path, level, remark, status, create_time, update_time) VALUES
(1, '办公家具', 0, '1', 1, '办公家具分类', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(2, '电子设备', 0, '2', 1, '电子设备分类', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(3, '家电设备', 0, '3', 1, '家电设备分类', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(4, '数码产品', 0, '4', 1, '数码产品分类', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 插入子节点
-- 办公家具子节点
INSERT INTO asset_type (id, name, parent_id, path, level, remark, status, create_time, update_time) VALUES
(5, '办公桌', 1, '1,5', 2, '办公桌椅', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(6, '办公椅', 1, '1,6', 2, '办公座椅', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 电子设备子节点
INSERT INTO asset_type (id, name, parent_id, path, level, remark, status, create_time, update_time) VALUES
(7, '笔记本电脑', 2, '2,7', 2, '笔记本电脑', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(8, '打印机', 2, '2,8', 2, '打印机', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(9, '台式电脑', 2, '2,9', 2, '台式电脑', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 家电设备子节点
INSERT INTO asset_type (id, name, parent_id, path, level, remark, status, create_time, update_time) VALUES
(10, '冰箱', 3, '3,10', 2, '冰箱', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(11, '空调', 3, '3,11', 2, '空调', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(12, '微波炉', 3, '3,12', 2, '微波炉', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 数码产品子节点
INSERT INTO asset_type (id, name, parent_id, path, level, remark, status, create_time, update_time) VALUES
(13, '扫描器', 4, '4,13', 2, '扫描器', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(14, '摄像机', 4, '4,14', 2, '摄像机', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(15, '投影仪', 4, '4,15', 2, '投影仪', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 插入资产位置示例数据
INSERT INTO asset_location (id, name, parent_id, path, level, remark, status, create_time, update_time) VALUES
(1, '办公室', 0, '1', 1, '办公区域', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(2, '会议室', 0, '2', 1, '会议区域', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 插入资产状态字典数据
INSERT INTO sys_dict (type, code, value, order_num, create_time, update_time) VALUES
('asset_status', 'idle', '闲置', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_status', 'in_use', '使用中', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_status', 'repairing', '维修中', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_status', 'scrapped', '已报废', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入资产来源字典数据
INSERT INTO sys_dict (type, code, value, order_num, create_time, update_time) VALUES
('asset_source', 'self_produced', '自产', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_source', 'purchased', '采购', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入计量单位字典数据
INSERT INTO sys_dict (type, code, value, order_num, create_time, update_time) VALUES
('asset_unit', 'unit', '台', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_unit', 'piece', '个', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_unit', 'set', '套', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_unit', 'sheet', '张', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

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

-- 插入供应商管理菜单和权限
-- 供应商管理
INSERT INTO sys_menu (id, name, path, component, parent_id, icon, code, type, status, order_num) VALUES
(51, '供应商管理', '/asset/supplier', 'business/asset-supplier/Index', 35, 'UserFilled', NULL, 'menu', true, 19);

-- 供应商操作权限
INSERT INTO sys_menu (id, name, path, component, parent_id, icon, code, type, status, order_num) VALUES
(52, '供应商列表', NULL, NULL, 51, NULL, 'asset:supplier:list', 'operation', true, 31),
(53, '供应商添加', NULL, NULL, 51, NULL, 'asset:supplier:add', 'operation', true, 32),
(54, '供应商编辑', NULL, NULL, 51, NULL, 'asset:supplier:edit', 'operation', true, 33),
(55, '供应商删除', NULL, NULL, 51, NULL, 'asset:supplier:delete', 'operation', true, 34);

-- 为admin角色分配供应商管理权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 51), (1, 52), (1, 53), (1, 54), (1, 55);

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

-- 插入资产盘点状态字典数据
INSERT INTO sys_dict (type, code, value, order_num, create_time, update_time) VALUES
('inventory_status', 'in_progress', '进行中', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inventory_status', 'completed', '已完成', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inventory_status', 'cancelled', '已取消', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入资产盘点明细状态字典数据
INSERT INTO sys_dict (type, code, value, order_num, create_time, update_time) VALUES
('inventory_detail_status', 'normal', '正常', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inventory_detail_status', 'overage', '盘盈', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inventory_detail_status', 'shortage', '盘亏', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);



-- 插入资产盘点管理菜单和权限
-- 资产盘点管理
INSERT INTO sys_menu (id, name, path, component, parent_id, icon, code, type, status, order_num) VALUES
(56, '资产盘点', '/asset/inventory', 'business/asset-inventory/Index', 35, 'DataAnalysis', NULL, 'menu', true, 20);

-- 资产盘点操作权限
INSERT INTO sys_menu (id, name, path, component, parent_id, icon, code, type, status, order_num) VALUES
(57, '资产盘点列表', NULL, NULL, 56, NULL, 'asset:inventory:list', 'operation', true, 35),
(58, '资产盘点添加', NULL, NULL, 56, NULL, 'asset:inventory:add', 'operation', true, 36),
(59, '资产盘点编辑', NULL, NULL, 56, NULL, 'asset:inventory:edit', 'operation', true, 37),
(60, '资产盘点删除', NULL, NULL, 56, NULL, 'asset:inventory:delete', 'operation', true, 38);

-- 为admin角色分配资产盘点管理权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 56), (1, 57), (1, 58), (1, 59), (1, 60);
