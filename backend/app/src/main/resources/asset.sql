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
  `approver_id` BIGINT COMMENT '审批人ID',
  `reject_reason` VARCHAR(500) COMMENT '拒绝原因',
  PRIMARY KEY (`id`),
  INDEX `idx_asset_id` (`asset_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_dept_id` (`dept_id`),
  INDEX `idx_approver_id` (`approver_id`)
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
-- 供应商管理
(1006, '供应商管理', '/asset/supplier', 'business/asset-supplier/Index', 1000, 'UserFilled', NULL, 'menu', true, 18),
-- 资产盘点管理
(1007, '资产盘点', '/asset/inventory', 'business/asset-inventory/Index', 1000, 'DataAnalysis', NULL, 'menu', true, 20),
-- 领用记录
(1033, '领用记录', '/asset/approval', 'business/asset/Approval', 1000, 'CheckCircle', NULL, 'menu', true, 23),
-- 退库记录
(1034, '退库记录', '/asset/return', 'business/asset/ReturnRecord', 1000, 'Return', NULL, 'menu', true, 24),
-- 转移记录
(1035, '转移记录', '/asset/transfer', 'business/asset/TransferRecord', 1000, 'Switch', NULL, 'menu', true, 25),
-- 资产报废
(1044, '资产报废', '/asset/scrap', 'business/asset/Scrap', 1000, 'Delete', NULL, 'menu', true, 26),
-- 资产处置
(1045, '资产处置', '/asset/dispose', 'business/asset/Dispose', 1000, 'Trash2', NULL, 'menu', true, 27),
-- 资产变更
(1046, '资产变更', '/asset/change', 'business/asset/Change', 1000, 'Edit', NULL, 'menu', true, 28),
-- 报废列表
(1047, '报废列表', '/asset/scrap-list', 'business/asset/ScrapList', 1000, 'List', NULL, 'menu', true, 29),
-- 折旧设置
(1048, '折旧设置', '/asset/depreciation', 'business/asset/Depreciation', 1000, 'Settings', NULL, 'menu', true, 30),
-- 采购管理
(3000, '采购管理', '/purchase', '', 0, 'ShoppingCart', NULL, 'menu', true, 20),
-- 采购管理二级菜单
(3001, '采购管理', '/purchase/list', 'business/purchase/Index', 3000, 'List', NULL, 'menu', true, 1),
-- 收货管理
(3002, '收货管理', '/purchase/receipt', 'business/purchase/Receipt', 3000, 'Inbox', NULL, 'menu', true, 2),
-- 入库管理
(3003, '入库管理', '/purchase/storage', 'business/purchase/Storage', 3000, 'Box', NULL, 'menu', true, 3),
-- 物资管理
(4000, '物资管理', '/material', '', 0, 'Box', NULL, 'menu', true, 21),
-- 物资列表
(4001, '物资列表', '/material/list', 'business/material/Index', 4000, 'List', NULL, 'menu', true, 1),
-- 批次列表
(4002, '批次列表', '/material/batch', 'business/material/Batch', 4000, 'FileText', NULL, 'menu', true, 2),
-- 物资入库
(4003, '物资入库', '/material/in', 'business/material/In', 4000, 'Inbox', NULL, 'menu', true, 3),
-- 物资出库
(4004, '物资出库', '/material/out', 'business/material/Out', 4000, 'Outbox', NULL, 'menu', true, 4),
-- 物资调拨
(4005, '物资调拨', '/material/transfer', 'business/material/Transfer', 4000, 'Switch', NULL, 'menu', true, 5),
-- 物资盘点
(4006, '物资盘点', '/material/inventory', 'business/material/Inventory', 4000, 'DataAnalysis', NULL, 'menu', true, 6),
-- 物资分类
(4007, '物资分类', '/material/category', 'business/material/Category', 4000, 'Layers', NULL, 'menu', true, 7),
-- 物资仓库
(4008, '物资仓库', '/material/warehouse', 'business/material/Warehouse', 4000, 'Home', NULL, 'menu', true, 8),
-- 领用记录
(4009, '领用记录', '/material/use', 'business/material/UseRecord', 4000, 'CheckCircle', NULL, 'menu', true, 9),
-- 资产统计
(5000, '资产统计', '/asset-statistics', '', 0, 'PieChart', NULL, 'menu', true, 22),
-- 分类统计
(5001, '分类统计', '/asset-statistics/category', 'business/asset-statistics/Category', 5000, 'Layers', NULL, 'menu', true, 1),
-- 位置统计
(5002, '位置统计', '/asset-statistics/location', 'business/asset-statistics/Location', 5000, 'Location', NULL, 'menu', true, 2),
-- 部门资产统计
(5003, '部门资产统计', '/asset-statistics/dept', 'business/asset-statistics/Dept', 5000, 'Team', NULL, 'menu', true, 3),
-- 员工资产统计
(5004, '员工资产统计', '/asset-statistics/employee', 'business/asset-statistics/Employee', 5000, 'User', NULL, 'menu', true, 4),
-- 物资统计
(6000, '物资统计', '/material-statistics', '', 0, 'PieChart', NULL, 'menu', true, 23),
-- 库存统计
(6001, '库存统计', '/material-statistics/stock', 'business/material-statistics/Stock', 6000, 'Box', NULL, 'menu', true, 1),
-- 进出统计
(6002, '进出统计', '/material-statistics/inout', 'business/material-statistics/InOut', 6000, 'Exchange', NULL, 'menu', true, 2),
-- 领用统计
(6003, '领用统计', '/material-statistics/use', 'business/material-statistics/Use', 6000, 'CheckCircle', NULL, 'menu', true, 3),
-- 维保管理
(7000, '维保管理', '/maintenance', '', 0, 'Tools', NULL, 'menu', true, 24),
-- 租借管理
(8000, '租借管理', '/rental', '', 0, 'Sell', NULL, 'menu', true, 25),
-- 个人中心主菜单
(2000, '个人中心', '/personal', '', 0, 'User', NULL, 'menu', true, 26),
-- 我的资产
(2001, '我的资产', '/personal/asset/my', 'business/asset/MyAsset', 2000, 'User', NULL, 'menu', true, 26),
-- 资产领用
(2002, '资产领用', '/personal/asset/apply', 'business/asset/Apply', 2000, 'Document', NULL, 'menu', true, 27),
-- 资产退库
(2008, '资产退库', '/personal/asset/return', 'business/asset/Return', 2000, 'Return', NULL, 'menu', true, 28),
-- 资产转移
(2009, '资产转移', '/personal/asset/transfer', 'business/asset/Transfer', 2000, 'Switch', NULL, 'menu', true, 29),
-- 资产报修
(2010, '资产报修', '/personal/asset/repair', 'business/asset/Repair', 2000, 'Tool', NULL, 'menu', true, 30);

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
(1036, '资产查询', NULL, NULL, 1003, NULL, 'asset:query', 'operation', true, 271),
(1017, '资产添加', NULL, NULL, 1003, NULL, 'asset:add', 'operation', true, 28),
(1018, '资产编辑', NULL, NULL, 1003, NULL, 'asset:edit', 'operation', true, 29),
(1019, '资产删除', NULL, NULL, 1003, NULL, 'asset:delete', 'operation', true, 30),
-- 领用审批操作
(1042, '领用审批列表', NULL, NULL, 1033, NULL, 'asset:approval:list', 'operation', true, 44),
(1043, '领用审批操作', NULL, NULL, 1033, NULL, 'asset:approval:operate', 'operation', true, 45),
-- 退库记录操作
(1038, '退库记录列表', NULL, NULL, 1034, NULL, 'asset:return:list', 'operation', true, 46),
(1039, '退库记录查看', NULL, NULL, 1034, NULL, 'asset:return:view', 'operation', true, 47),
-- 转移记录操作
(1040, '转移记录列表', NULL, NULL, 1035, NULL, 'asset:transfer:list', 'operation', true, 48),
(1041, '转移记录查看', NULL, NULL, 1035, NULL, 'asset:transfer:view', 'operation', true, 49),
-- 资产报废操作
(1049, '资产报废列表', NULL, NULL, 1044, NULL, 'asset:scrap:list', 'operation', true, 50),
(1050, '资产报废添加', NULL, NULL, 1044, NULL, 'asset:scrap:add', 'operation', true, 51),
(1051, '资产报废编辑', NULL, NULL, 1044, NULL, 'asset:scrap:edit', 'operation', true, 52),
(1052, '资产报废删除', NULL, NULL, 1044, NULL, 'asset:scrap:delete', 'operation', true, 53),
-- 资产处置操作
(1053, '资产处置列表', NULL, NULL, 1045, NULL, 'asset:dispose:list', 'operation', true, 54),
(1054, '资产处置添加', NULL, NULL, 1045, NULL, 'asset:dispose:add', 'operation', true, 55),
(1055, '资产处置编辑', NULL, NULL, 1045, NULL, 'asset:dispose:edit', 'operation', true, 56),
(1056, '资产处置删除', NULL, NULL, 1045, NULL, 'asset:dispose:delete', 'operation', true, 57),
-- 资产变更操作
(1057, '资产变更列表', NULL, NULL, 1046, NULL, 'asset:change:list', 'operation', true, 58),
(1058, '资产变更添加', NULL, NULL, 1046, NULL, 'asset:change:add', 'operation', true, 59),
(1059, '资产变更编辑', NULL, NULL, 1046, NULL, 'asset:change:edit', 'operation', true, 60),
(1060, '资产变更删除', NULL, NULL, 1046, NULL, 'asset:change:delete', 'operation', true, 61),
-- 报废列表操作
(1061, '报废列表查看', NULL, NULL, 1047, NULL, 'asset:scrap:list:view', 'operation', true, 62),
-- 折旧设置操作
(1062, '折旧设置列表', NULL, NULL, 1048, NULL, 'asset:depreciation:list', 'operation', true, 63),
(1063, '折旧设置添加', NULL, NULL, 1048, NULL, 'asset:depreciation:add', 'operation', true, 64),
(1064, '折旧设置编辑', NULL, NULL, 1048, NULL, 'asset:depreciation:edit', 'operation', true, 65),-- 折旧设置删除
(1065, '折旧设置删除', NULL, NULL, 1048, NULL, 'asset:depreciation:delete', 'operation', true, 66),
-- 物资列表操作
(4010, '物资列表', NULL, NULL, 4001, NULL, 'material:list', 'operation', true, 1),
(4011, '物资添加', NULL, NULL, 4001, NULL, 'material:add', 'operation', true, 2),
(4012, '物资编辑', NULL, NULL, 4001, NULL, 'material:edit', 'operation', true, 3),
(4013, '物资删除', NULL, NULL, 4001, NULL, 'material:delete', 'operation', true, 4),
-- 批次列表操作
(4014, '批次列表', NULL, NULL, 4002, NULL, 'material:batch:list', 'operation', true, 5),
(4015, '批次添加', NULL, NULL, 4002, NULL, 'material:batch:add', 'operation', true, 6),
(4016, '批次编辑', NULL, NULL, 4002, NULL, 'material:batch:edit', 'operation', true, 7),
(4017, '批次删除', NULL, NULL, 4002, NULL, 'material:batch:delete', 'operation', true, 8),
-- 物资入库操作
(4018, '物资入库列表', NULL, NULL, 4003, NULL, 'material:in:list', 'operation', true, 9),
(4019, '物资入库添加', NULL, NULL, 4003, NULL, 'material:in:add', 'operation', true, 10),
(4020, '物资入库编辑', NULL, NULL, 4003, NULL, 'material:in:edit', 'operation', true, 11),
(4021, '物资入库删除', NULL, NULL, 4003, NULL, 'material:in:delete', 'operation', true, 12),
-- 物资出库操作
(4022, '物资出库列表', NULL, NULL, 4004, NULL, 'material:out:list', 'operation', true, 13),
(4023, '物资出库添加', NULL, NULL, 4004, NULL, 'material:out:add', 'operation', true, 14),
(4024, '物资出库编辑', NULL, NULL, 4004, NULL, 'material:out:edit', 'operation', true, 15),
(4025, '物资出库删除', NULL, NULL, 4004, NULL, 'material:out:delete', 'operation', true, 16),
-- 物资调拨操作
(4026, '物资调拨列表', NULL, NULL, 4005, NULL, 'material:transfer:list', 'operation', true, 17),
(4027, '物资调拨添加', NULL, NULL, 4005, NULL, 'material:transfer:add', 'operation', true, 18),
(4028, '物资调拨编辑', NULL, NULL, 4005, NULL, 'material:transfer:edit', 'operation', true, 19),
(4029, '物资调拨删除', NULL, NULL, 4005, NULL, 'material:transfer:delete', 'operation', true, 20),
-- 物资盘点操作
(4030, '物资盘点列表', NULL, NULL, 4006, NULL, 'material:inventory:list', 'operation', true, 21),
(4031, '物资盘点添加', NULL, NULL, 4006, NULL, 'material:inventory:add', 'operation', true, 22),
(4032, '物资盘点编辑', NULL, NULL, 4006, NULL, 'material:inventory:edit', 'operation', true, 23),
(4033, '物资盘点删除', NULL, NULL, 4006, NULL, 'material:inventory:delete', 'operation', true, 24),
-- 物资分类操作
(4034, '物资分类列表', NULL, NULL, 4007, NULL, 'material:category:list', 'operation', true, 25),
(4035, '物资分类添加', NULL, NULL, 4007, NULL, 'material:category:add', 'operation', true, 26),
(4036, '物资分类编辑', NULL, NULL, 4007, NULL, 'material:category:edit', 'operation', true, 27),
(4037, '物资分类删除', NULL, NULL, 4007, NULL, 'material:category:delete', 'operation', true, 28),
-- 物资仓库操作
(4038, '物资仓库列表', NULL, NULL, 4008, NULL, 'material:warehouse:list', 'operation', true, 29),
(4039, '物资仓库添加', NULL, NULL, 4008, NULL, 'material:warehouse:add', 'operation', true, 30),
(4040, '物资仓库编辑', NULL, NULL, 4008, NULL, 'material:warehouse:edit', 'operation', true, 31),
(4041, '物资仓库删除', NULL, NULL, 4008, NULL, 'material:warehouse:delete', 'operation', true, 32),
-- 领用记录操作
(4042, '领用记录列表', NULL, NULL, 4009, NULL, 'material:use:list', 'operation', true, 33),
(4043, '领用记录添加', NULL, NULL, 4009, NULL, 'material:use:add', 'operation', true, 34),
(4044, '领用记录编辑', NULL, NULL, 4009, NULL, 'material:use:edit', 'operation', true, 35),-- 领用记录删除
(4045, '领用记录删除', NULL, NULL, 4009, NULL, 'material:use:delete', 'operation', true, 36),
-- 采购管理操作
(3004, '采购管理列表', NULL, NULL, 3001, NULL, 'purchase:list', 'operation', true, 1),
(3005, '采购管理添加', NULL, NULL, 3001, NULL, 'purchase:add', 'operation', true, 2),
(3006, '采购管理编辑', NULL, NULL, 3001, NULL, 'purchase:edit', 'operation', true, 3),
(3007, '采购管理删除', NULL, NULL, 3001, NULL, 'purchase:delete', 'operation', true, 4),
-- 收货管理操作
(3008, '收货管理列表', NULL, NULL, 3002, NULL, 'purchase:receipt:list', 'operation', true, 5),
(3009, '收货管理添加', NULL, NULL, 3002, NULL, 'purchase:receipt:add', 'operation', true, 6),
(3010, '收货管理编辑', NULL, NULL, 3002, NULL, 'purchase:receipt:edit', 'operation', true, 7),
(3011, '收货管理删除', NULL, NULL, 3002, NULL, 'purchase:receipt:delete', 'operation', true, 8),
-- 入库管理操作
(3012, '入库管理列表', NULL, NULL, 3003, NULL, 'purchase:storage:list', 'operation', true, 9),
(3013, '入库管理添加', NULL, NULL, 3003, NULL, 'purchase:storage:add', 'operation', true, 10),
(3014, '入库管理编辑', NULL, NULL, 3003, NULL, 'purchase:storage:edit', 'operation', true, 11),-- 入库管理删除
(3015, '入库管理删除', NULL, NULL, 3003, NULL, 'purchase:storage:delete', 'operation', true, 12),
-- 分类统计操作
(5005, '分类统计查看', NULL, NULL, 5001, NULL, 'asset:statistics:category:view', 'operation', true, 1),
-- 位置统计操作
(5006, '位置统计查看', NULL, NULL, 5002, NULL, 'asset:statistics:location:view', 'operation', true, 2),
-- 部门资产统计操作
(5007, '部门资产统计查看', NULL, NULL, 5003, NULL, 'asset:statistics:dept:view', 'operation', true, 3),
-- 员工资产统计操作
(5008, '员工资产统计查看', NULL, NULL, 5004, NULL, 'asset:statistics:employee:view', 'operation', true, 4),
-- 库存统计操作
(6004, '库存统计查看', NULL, NULL, 6001, NULL, 'material:statistics:stock:view', 'operation', true, 1),
-- 进出统计操作
(6005, '进出统计查看', NULL, NULL, 6002, NULL, 'material:statistics:inout:view', 'operation', true, 2),
-- 领用统计操作
(6006, '领用统计查看', NULL, NULL, 6003, NULL, 'material:statistics:use:view', 'operation', true, 3),
-- 供应商操作权限
(1025, '供应商列表', NULL, NULL, 1006, NULL, 'asset:supplier:list', 'operation', true, 31),
(1026, '供应商添加', NULL, NULL, 1006, NULL, 'asset:supplier:add', 'operation', true, 32),
(1027, '供应商编辑', NULL, NULL, 1006, NULL, 'asset:supplier:edit', 'operation', true, 33),
(1028, '供应商删除', NULL, NULL, 1006, NULL, 'asset:supplier:delete', 'operation', true, 34),
-- 资产盘点操作权限
(1029, '资产盘点列表', NULL, NULL, 1007, NULL, 'asset:inventory:list', 'operation', true, 35),
(1030, '资产盘点添加', NULL, NULL, 1007, NULL, 'asset:inventory:add', 'operation', true, 36),
(1031, '资产盘点编辑', NULL, NULL, 1007, NULL, 'asset:inventory:edit', 'operation', true, 37),
(1032, '资产盘点删除', NULL, NULL, 1007, NULL, 'asset:inventory:delete', 'operation', true, 38),
-- 我的资产操作
(2003, '我的资产列表', NULL, NULL, 2001, NULL, 'asset:my:list', 'operation', true, 39),
-- 资产领用操作
(2004, '资产领用列表', NULL, NULL, 2002, NULL, 'asset:apply:list', 'operation', true, 40),
(2005, '资产领用添加', NULL, NULL, 2002, NULL, 'asset:apply:add', 'operation', true, 41),
(2006, '资产领用编辑', NULL, NULL, 2002, NULL, 'asset:apply:edit', 'operation', true, 42),
(2007, '资产领用删除', NULL, NULL, 2002, NULL, 'asset:apply:delete', 'operation', true, 43),
-- 资产退库操作
(2011, '资产退库列表', NULL, NULL, 2008, NULL, 'asset:return:list', 'operation', true, 44),
(2012, '资产退库添加', NULL, NULL, 2008, NULL, 'asset:return:add', 'operation', true, 45),
(2013, '资产退库编辑', NULL, NULL, 2008, NULL, 'asset:return:edit', 'operation', true, 46),
(2014, '资产退库删除', NULL, NULL, 2008, NULL, 'asset:return:delete', 'operation', true, 47),
-- 资产转移操作
(2015, '资产转移列表', NULL, NULL, 2009, NULL, 'asset:transfer:list', 'operation', true, 48),
(2016, '资产转移添加', NULL, NULL, 2009, NULL, 'asset:transfer:add', 'operation', true, 49),
(2017, '资产转移编辑', NULL, NULL, 2009, NULL, 'asset:transfer:edit', 'operation', true, 50),
(2018, '资产转移删除', NULL, NULL, 2009, NULL, 'asset:transfer:delete', 'operation', true, 51),
-- 资产报修操作
(2019, '资产报修列表', NULL, NULL, 2010, NULL, 'asset:repair:list', 'operation', true, 52),
(2020, '资产报修添加', NULL, NULL, 2010, NULL, 'asset:repair:add', 'operation', true, 53),
(2021, '资产报修编辑', NULL, NULL, 2010, NULL, 'asset:repair:edit', 'operation', true, 54),
(2022, '资产报修删除', NULL, NULL, 2010, NULL, 'asset:repair:delete', 'operation', true, 55);

-- 为admin角色分配权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
-- 资产管理权限
(1, 1000), (1, 1001), (1, 1002), (1, 1003), (1, 1033), (1, 1034), (1, 1035), (1, 1044), (1, 1045), (1, 1046), (1, 1047), (1, 1048),
(1, 1008), (1, 1009), (1, 1010), (1, 1011),
(1, 1012), (1, 1013), (1, 1014), (1, 1015),
(1, 1016), (1, 1036), (1, 1017), (1, 1018), (1, 1019), (1, 1042), (1, 1043), (1, 1038), (1, 1039), (1, 1040), (1, 1041), (1, 1049), (1, 1050), (1, 1051), (1, 1052), (1, 1053), (1, 1054), (1, 1055), (1, 1056), (1, 1057), (1, 1058), (1, 1059), (1, 1060), (1, 1061), (1, 1062), (1, 1063), (1, 1064), (1, 1065),
-- 供应商管理权限
(1, 1006), (1, 1025), (1, 1026), (1, 1027), (1, 1028),
-- 资产盘点管理权限
(1, 1007), (1, 1029), (1, 1030), (1, 1031), (1, 1032),
-- 采购管理权限
(1, 3000), (1, 3001), (1, 3002), (1, 3003),
(1, 3004), (1, 3005), (1, 3006), (1, 3007), (1, 3008), (1, 3009), (1, 3010), (1, 3011), (1, 3012), (1, 3013), (1, 3014), (1, 3015),
-- 物资管理权限
(1, 4000), (1, 4001), (1, 4002), (1, 4003), (1, 4004), (1, 4005), (1, 4006), (1, 4007), (1, 4008), (1, 4009),
(1, 4010), (1, 4011), (1, 4012), (1, 4013), (1, 4014), (1, 4015), (1, 4016), (1, 4017), (1, 4018), (1, 4019), (1, 4020), (1, 4021), (1, 4022), (1, 4023), (1, 4024), (1, 4025), (1, 4026), (1, 4027), (1, 4028), (1, 4029), (1, 4030), (1, 4031), (1, 4032), (1, 4033), (1, 4034), (1, 4035), (1, 4036), (1, 4037), (1, 4038), (1, 4039), (1, 4040), (1, 4041), (1, 4042), (1, 4043), (1, 4044), (1, 4045),
-- 资产统计权限
(1, 5000), (1, 5001), (1, 5002), (1, 5003), (1, 5004), (1, 5005), (1, 5006), (1, 5007), (1, 5008),
-- 物资统计权限
(1, 6000), (1, 6001), (1, 6002), (1, 6003), (1, 6004), (1, 6005), (1, 6006),
-- 维保管理权限
(1, 7000),
-- 租借管理权限
(1, 8000),
-- 个人中心权限
(1, 2000), (1, 2001), (1, 2002), (1, 2003), (1, 2004), (1, 2005), (1, 2006), (1, 2007),
-- 资产退库权限
(1, 2008), (1, 2011), (1, 2012), (1, 2013), (1, 2014),
-- 资产转移权限
(1, 2009), (1, 2015), (1, 2016), (1, 2017), (1, 2018),
-- 资产报修权限
(1, 2010), (1, 2019), (1, 2020), (1, 2021), (1, 2022);

-- 插入系统配置
INSERT INTO sys_config (config_key, value, description, create_time, update_time) VALUES
('system.print_service_url', 'http://127.0.0.1:8000/api', '打印服务URL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
