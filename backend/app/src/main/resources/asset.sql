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

-- 插入资产位置示例数据
INSERT INTO asset_location (id, name, parent_id, path, level, remark, status, create_time, update_time) VALUES
(1, '办公室', 0, '1', 1, '办公区域', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(2, '会议室', 0, '2', 1, '会议区域', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

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

-- 资产领用表
CREATE TABLE IF NOT EXISTS `asset_apply` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `asset_id` BIGINT NOT NULL COMMENT '资产ID',
  `user_id` BIGINT NOT NULL COMMENT '领用用户ID',
  `dept_id` BIGINT COMMENT '领用部门ID',
  `apply_time` BIGINT NOT NULL COMMENT '申请时间',
  `approval_time` BIGINT COMMENT '审批时间',
  `status` VARCHAR(50) NOT NULL COMMENT '状态（待审批、已审批、已拒绝）',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` BIGINT NOT NULL COMMENT '创建时间',
  `update_time` BIGINT NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_asset_id` (`asset_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产领用表';

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

-- 资产领用状态字典数据
INSERT INTO sys_dict (type, code, value, order_num, create_time, update_time) VALUES
('asset_apply_status', 'pending', '待审批', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_apply_status', 'approved', '已审批', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('asset_apply_status', 'rejected', '已拒绝', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入菜单和权限
-- 资产管理主菜单
INSERT INTO sys_menu (id, name, path, component, parent_id, icon, code, type, status, order_num) VALUES
(1000, '资产管理', '/asset', '', 0, 'Briefcase', NULL, 'menu', true, 15),
-- 资产类型管理
(1001, '资产类型管理', '/asset/type', 'business/asset-type/Index', 1000, 'List', NULL, 'menu', true, 16),
-- 资产位置管理
(1002, '资产位置管理', '/asset/location', 'business/asset-location/Index', 1000, 'Location', NULL, 'menu', true, 17),
-- 资产管理
(1003, '资产管理', '/asset/list', 'business/asset/Index', 1000, 'Inventory', NULL, 'menu', true, 19),
-- 我的资产
(1004, '我的资产', '/asset/my', 'business/asset/MyAsset', 1000, 'User', NULL, 'menu', true, 21),
-- 资产领用
(1005, '资产领用', '/asset/apply', 'business/asset/Apply', 1000, 'Document', NULL, 'menu', true, 22),
-- 供应商管理
(1006, '供应商管理', '/asset/supplier', 'business/asset-supplier/Index', 1000, 'UserFilled', NULL, 'menu', true, 18),
-- 资产盘点管理
(1007, '资产盘点', '/asset/inventory', 'business/asset-inventory/Index', 1000, 'DataAnalysis', NULL, 'menu', true, 20);

-- 操作权限
-- 资产类型操作
INSERT INTO sys_menu (id, name, path, component, parent_id, icon, code, type, status, order_num) VALUES
(1008, '资产类型列表', NULL, NULL, 1001, NULL, 'asset:type:list', 'operation', true, 19),
(1009, '资产类型添加', NULL, NULL, 1001, NULL, 'asset:type:add', 'operation', true, 20),
(1010, '资产类型编辑', NULL, NULL, 1001, NULL, 'asset:type:edit', 'operation', true, 21),
(1011, '资产类型删除', NULL, NULL, 1001, NULL, 'asset:type:delete', 'operation', true, 22),
-- 资产位置操作
(1012, '资产位置列表', NULL, NULL, 1002, NULL, 'asset:location:list', 'operation', true, 23),
(1013, '资产位置添加', NULL, NULL, 1002, NULL, 'asset:location:add', 'operation', true, 24),
(1014, '资产位置编辑', NULL, NULL, 1002, NULL, 'asset:location:edit', 'operation', true, 25),
(1015, '资产位置删除', NULL, NULL, 1002, NULL, 'asset:location:delete', 'operation', true, 26),
-- 资产操作
(1016, '资产列表', NULL, NULL, 1003, NULL, 'asset:list', 'operation', true, 27),
(1017, '资产添加', NULL, NULL, 1003, NULL, 'asset:add', 'operation', true, 28),
(1018, '资产编辑', NULL, NULL, 1003, NULL, 'asset:edit', 'operation', true, 29),
(1019, '资产删除', NULL, NULL, 1003, NULL, 'asset:delete', 'operation', true, 30),
-- 我的资产操作
(1020, '我的资产列表', NULL, NULL, 1004, NULL, 'asset:my:list', 'operation', true, 39),
-- 资产领用操作
(1021, '资产领用列表', NULL, NULL, 1005, NULL, 'asset:apply:list', 'operation', true, 40),
(1022, '资产领用添加', NULL, NULL, 1005, NULL, 'asset:apply:add', 'operation', true, 41),
(1023, '资产领用编辑', NULL, NULL, 1005, NULL, 'asset:apply:edit', 'operation', true, 42),
(1024, '资产领用删除', NULL, NULL, 1005, NULL, 'asset:apply:delete', 'operation', true, 43),
-- 供应商操作权限
(1025, '供应商列表', NULL, NULL, 1006, NULL, 'asset:supplier:list', 'operation', true, 31),
(1026, '供应商添加', NULL, NULL, 1006, NULL, 'asset:supplier:add', 'operation', true, 32),
(1027, '供应商编辑', NULL, NULL, 1006, NULL, 'asset:supplier:edit', 'operation', true, 33),
(1028, '供应商删除', NULL, NULL, 1006, NULL, 'asset:supplier:delete', 'operation', true, 34),
-- 资产盘点操作权限
(1029, '资产盘点列表', NULL, NULL, 1007, NULL, 'asset:inventory:list', 'operation', true, 35),
(1030, '资产盘点添加', NULL, NULL, 1007, NULL, 'asset:inventory:add', 'operation', true, 36),
(1031, '资产盘点编辑', NULL, NULL, 1007, NULL, 'asset:inventory:edit', 'operation', true, 37),
(1032, '资产盘点删除', NULL, NULL, 1007, NULL, 'asset:inventory:delete', 'operation', true, 38);

-- 为admin角色分配权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
-- 资产管理权限
(1, 1000), (1, 1001), (1, 1002), (1, 1003), (1, 1004), (1, 1005),
(1, 1008), (1, 1009), (1, 1010), (1, 1011),
(1, 1012), (1, 1013), (1, 1014), (1, 1015),
(1, 1016), (1, 1017), (1, 1018), (1, 1019), (1, 1020), (1, 1021), (1, 1022), (1, 1023), (1, 1024),
-- 供应商管理权限
(1, 1006), (1, 1025), (1, 1026), (1, 1027), (1, 1028),
-- 资产盘点管理权限
(1, 1007), (1, 1029), (1, 1030), (1, 1031), (1, 1032);

-- 插入系统配置
INSERT INTO sys_config (config_key, value, description, create_time, update_time) VALUES
('system.print_service_url', 'http://127.0.0.1:8000/api', '打印服务URL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
