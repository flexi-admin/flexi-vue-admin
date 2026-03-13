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
  `supplier` VARCHAR(255) COMMENT '供应商',
  `purchase_date` VARCHAR(20) COMMENT '购买日期',
  `price` DECIMAL(10,2) COMMENT '价格',
  `status` VARCHAR(50) COMMENT '状态',
  `remark` VARCHAR(500) COMMENT '备注',
  `is_deleted` INT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `create_time` BIGINT NOT NULL COMMENT '创建时间',
  `update_time` BIGINT NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_type_id` (`type_id`),
  INDEX `idx_location_id` (`location_id`)
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
