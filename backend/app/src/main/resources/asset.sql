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

-- 插入资产类型示例数据
-- 插入父节点
INSERT INTO asset_type (id, name, code, parent_id, path, level, remark, status, create_time, update_time) VALUES
(1, '办公家具', 'BGJJ', 0, '1', 1, '办公家具分类', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(2, '电子设备', 'DZSB', 0, '2', 1, '电子设备分类', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(3, '家电设备', 'JDSB', 0, '3', 1, '家电设备分类', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(4, '数码产品', 'SMPC', 0, '4', 1, '数码产品分类', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 插入子节点
-- 办公家具子节点
INSERT INTO asset_type (id, name, code, parent_id, path, level, remark, status, create_time, update_time) VALUES
(5, '办公桌', 'BGZ', 1, '1,5', 2, '办公桌椅', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(6, '办公椅', 'BGY', 1, '1,6', 2, '办公座椅', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 电子设备子节点
INSERT INTO asset_type (id, name, code, parent_id, path, level, remark, status, create_time, update_time) VALUES
(7, '笔记本电脑', 'BJB', 2, '2,7', 2, '笔记本电脑', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(8, '打印机', 'DYJ', 2, '2,8', 2, '打印机', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(9, '台式电脑', 'TSB', 2, '2,9', 2, '台式电脑', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 家电设备子节点
INSERT INTO asset_type (id, name, code, parent_id, path, level, remark, status, create_time, update_time) VALUES
(10, '冰箱', 'BX', 3, '3,10', 2, '冰箱', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(11, '空调', 'KT', 3, '3,11', 2, '空调', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(12, '微波炉', 'WBL', 3, '3,12', 2, '微波炉', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 数码产品子节点
INSERT INTO asset_type (id, name, code, parent_id, path, level, remark, status, create_time, update_time) VALUES
(13, '扫描器', 'SMQ', 4, '4,13', 2, '扫描器', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(14, '摄像机', 'SXG', 4, '4,14', 2, '摄像机', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(15, '投影仪', 'TYJ', 4, '4,15', 2, '投影仪', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

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
  `type_code` VARCHAR(50) NOT NULL COMMENT '类型编码',
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
(10000, '资产管理', '/asset', '', 0, 'Briefcase', NULL, 'menu', true, 15),
-- 资产类型管理
(10001, '资产类型管理', '/asset/type', 'business/asset-type/Index', 10000, 'List', NULL, 'menu', true, 16),
-- 资产位置管理
(10002, '资产位置管理', '/asset/location', 'business/asset-location/Index', 10000, 'Location', NULL, 'menu', true, 17),
-- 资产管理
(10003, '资产管理', '/asset/list', 'business/asset/Index', 10000, 'Inventory', NULL, 'menu', true, 19),
-- 资产盘点管理
(10004, '资产盘点', '/asset/inventory', 'business/asset-inventory/Index', 10000, 'DataAnalysis', NULL, 'menu', true, 20),
-- 领用记录
(10005, '资产领用记录', '/asset/approval', 'business/asset/Approval', 10000, 'CheckCircle', NULL, 'menu', true, 23),
-- 退库记录
(10006, '资产退库记录', '/asset/return', 'business/asset/ReturnRecord', 10000, 'Return', NULL, 'menu', true, 24),
-- 转移记录
(10007, '资产转移记录', '/asset/transfer', 'business/asset/TransferRecord', 10000, 'Switch', NULL, 'menu', true, 25),
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
(10101, '采购管理', '/purchase/list', 'business/purchase/Index', 10100, 'List', NULL, 'menu', true, 1),
-- 收货管理
(10102, '收货管理', '/purchase/receipt', 'business/purchase/Receipt', 10100, 'Inbox', NULL, 'menu', true, 2),
-- 入库管理
(10103, '入库管理', '/purchase/storage', 'business/purchase/Storage', 10100, 'Box', NULL, 'menu', true, 3),
-- 供应商管理
(10104, '供应商管理', '/purchase/supplier', 'business/supplier/Index', 10100, 'User', NULL, 'menu', true, 4),
-- 物资管理
(10200, '物资管理', '/material', '', 0, 'Box', NULL, 'menu', true, 21),
-- 物资列表
(10201, '物资列表', '/material/list', 'business/material/Index', 10200, 'List', NULL, 'menu', true, 1),
-- 批次列表
(10202, '批次列表', '/material/batch', 'business/material/Batch', 10200, 'FileText', NULL, 'menu', true, 2),
-- 物资入库
(10203, '物资入库', '/material/in', 'business/material/In', 10200, 'Inbox', NULL, 'menu', true, 4),
-- 物资出库
(10204, '物资出库', '/material/out', 'business/material/Out', 10200, 'Outbox', NULL, 'menu', true, 4),
-- 物资调拨
(10205, '物资调拨', '/material/transfer', 'business/material/Transfer', 10200, 'Exchange', NULL, 'menu', true, 5),
-- 物资盘点
(10206, '物资盘点', '/material/inventory', 'business/material/Inventory', 10200, 'DataAnalysis', NULL, 'menu', true, 6),
-- 物资分类
(10207, '物资分类', '/material/category', 'business/material-category/Index', 10200, 'Layers', NULL, 'menu', true, 2),
-- 物资品类
(10210, '物资品类', '/material/brand', 'business/material-brand/Index', 10200, 'PriceTag', NULL, 'menu', true, 3),
-- 物资仓库
(10208, '物资仓库', '/material/warehouse', 'business/material-warehouse/Index', 10200, 'Home', NULL, 'menu', true, 4),
-- 物资管理
(10212, '物资管理', '/material', 'business/material/Index', 10200, 'Inventory', NULL, 'menu', true, 5),
-- 领用记录
(10209, '物资领用记录', '/material/use', 'business/material/UseRecord', 10200, 'CheckCircle', NULL, 'menu', true, 9),
-- 资产统计
(10300, '资产统计', '/asset-statistics', '', 0, 'PieChart', NULL, 'menu', true, 22),
-- 分类统计
(10301, '分类统计', '/asset-statistics/category', 'business/asset-statistics/Category', 10300, 'Layers', NULL, 'menu', true, 1),
-- 位置统计
(10302, '位置统计', '/asset-statistics/location', 'business/asset-statistics/Location', 10300, 'Location', NULL, 'menu', true, 2),
-- 部门资产统计
(10303, '部门资产统计', '/asset-statistics/dept', 'business/asset-statistics/Dept', 10300, 'Team', NULL, 'menu', true, 3),
-- 员工资产统计
(10304, '员工资产统计', '/asset-statistics/employee', 'business/asset-statistics/Employee', 10300, 'User', NULL, 'menu', true, 4),
-- 物资统计
(10400, '物资统计', '/material-statistics', '', 0, 'PieChart', NULL, 'menu', true, 23),
-- 库存统计
(10401, '库存统计', '/material-statistics/stock', 'business/material-statistics/Stock', 10400, 'Box', NULL, 'menu', true, 1),
-- 进出统计
(10402, '进出统计', '/material-statistics/inout', 'business/material-statistics/InOut', 10400, 'Exchange', NULL, 'menu', true, 2),
-- 领用统计
(10403, '领用统计', '/material-statistics/use', 'business/material-statistics/Use', 10400, 'CheckCircle', NULL, 'menu', true, 3),
-- 维保管理
(10500, '维保管理', '/maintenance', '', 0, 'Tools', NULL, 'menu', true, 24),
-- 数据大屏
(10600, '数据大屏', '/dashboard', '', 0, 'DataAnalysis', NULL, 'menu', true, 25),
-- 个人中心主菜单
(10700, '个人中心', '/personal', '', 0, 'User', NULL, 'menu', true, 26),
-- 我的资产
(10701, '我的资产', '/personal/asset/my', 'business/asset/MyAsset', 10700, 'User', NULL, 'menu', true, 26),
-- 资产领用
(10702, '资产领用', '/personal/asset/apply', 'business/asset/Apply', 10700, 'Document', NULL, 'menu', true, 27),
-- 资产退库
(10703, '资产退库', '/personal/asset/return', 'business/asset/Return', 10700, 'Return', NULL, 'menu', true, 28),
-- 资产转移
(10704, '资产转移', '/personal/asset/transfer', 'business/asset/Transfer', 10700, 'Switch', NULL, 'menu', true, 29),
-- 资产报修
(10705, '资产报修', '/personal/asset/repair', 'business/asset/Repair', 10700, 'Tool', NULL, 'menu', true, 30);

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
-- 领用审批操作
(1000501, '领用记录列表', NULL, NULL, 10005, NULL, 'asset:approval:list', 'operation', true, 1),
(1000502, '领用记录操作', NULL, NULL, 10005, NULL, 'asset:approval:operate', 'operation', true, 2),
-- 退库记录操作
(1000601, '退库记录列表', NULL, NULL, 10006, NULL, 'asset:return:list', 'operation', true, 1),
(1000602, '退库记录操作', NULL, NULL, 10006, NULL, 'asset:return:operate', 'operation', true, 2),
-- 转移记录操作
(1000701, '转移记录列表', NULL, NULL, 10007, NULL, 'asset:transfer:list', 'operation', true, 1),
(1000702, '转移记录操作', NULL, NULL, 10007, NULL, 'asset:transfer:operate', 'operation', true, 2),
-- 资产报废操作
(1000801, '资产报废列表', NULL, NULL, 10008, NULL, 'asset:scrap:list', 'operation', true, 1),
(1000802, '资产报废添加', NULL, NULL, 10008, NULL, 'asset:scrap:add', 'operation', true, 2),
(1000803, '资产报废编辑', NULL, NULL, 10008, NULL, 'asset:scrap:edit', 'operation', true, 3),
(1000804, '资产报废删除', NULL, NULL, 10008, NULL, 'asset:scrap:delete', 'operation', true, 4),
-- 资产处置操作
(1000901, '资产处置列表', NULL, NULL, 10009, NULL, 'asset:dispose:list', 'operation', true, 1),
(1000902, '资产处置添加', NULL, NULL, 10009, NULL, 'asset:dispose:add', 'operation', true, 2),
(1000903, '资产处置编辑', NULL, NULL, 10009, NULL, 'asset:dispose:edit', 'operation', true, 3),
(1000904, '资产处置删除', NULL, NULL, 10009, NULL, 'asset:dispose:delete', 'operation', true, 4),
-- 资产变更操作
(1001001, '资产变更列表', NULL, NULL, 10010, NULL, 'asset:change:list', 'operation', true, 1),
(1001002, '资产变更添加', NULL, NULL, 10010, NULL, 'asset:change:add', 'operation', true, 2),
(1001003, '资产变更编辑', NULL, NULL, 10010, NULL, 'asset:change:edit', 'operation', true, 3),
(1001004, '资产变更删除', NULL, NULL, 10010, NULL, 'asset:change:delete', 'operation', true, 4),
-- 报废列表操作
(1001101, '报废列表查看', NULL, NULL, 10011, NULL, 'asset:scrap:list:view', 'operation', true, 1),
-- 折旧设置操作
(1001201, '折旧设置列表', NULL, NULL, 10012, NULL, 'asset:depreciation:list', 'operation', true, 1),
(1001202, '折旧设置添加', NULL, NULL, 10012, NULL, 'asset:depreciation:add', 'operation', true, 2),
(1001203, '折旧设置编辑', NULL, NULL, 10012, NULL, 'asset:depreciation:edit', 'operation', true, 3),
(1001204, '折旧设置删除', NULL, NULL, 10012, NULL, 'asset:depreciation:delete', 'operation', true, 4),
-- 采购管理操作
(1010101, '采购管理列表', NULL, NULL, 10101, NULL, 'purchase:list', 'operation', true, 1),
(1010102, '采购管理添加', NULL, NULL, 10101, NULL, 'purchase:add', 'operation', true, 2),
(1010103, '采购管理编辑', NULL, NULL, 10101, NULL, 'purchase:edit', 'operation', true, 3),
(1010104, '采购管理删除', NULL, NULL, 10101, NULL, 'purchase:delete', 'operation', true, 4),
-- 收货管理操作
(1010201, '收货管理列表', NULL, NULL, 10102, NULL, 'purchase:receipt:list', 'operation', true, 1),
(1010202, '收货管理添加', NULL, NULL, 10102, NULL, 'purchase:receipt:add', 'operation', true, 2),
(1010203, '收货管理编辑', NULL, NULL, 10102, NULL, 'purchase:receipt:edit', 'operation', true, 3),
(1010204, '收货管理删除', NULL, NULL, 10102, NULL, 'purchase:receipt:delete', 'operation', true, 4),
-- 入库管理操作
(1010301, '入库管理列表', NULL, NULL, 10103, NULL, 'purchase:storage:list', 'operation', true, 1),
(1010302, '入库管理添加', NULL, NULL, 10103, NULL, 'purchase:storage:add', 'operation', true, 2),
(1010303, '入库管理编辑', NULL, NULL, 10103, NULL, 'purchase:storage:edit', 'operation', true, 3),
(1010304, '入库管理删除', NULL, NULL, 10103, NULL, 'purchase:storage:delete', 'operation', true, 4),
-- 供应商操作权限
(1010401, '供应商列表', NULL, NULL, 10104, NULL, 'asset:supplier:list', 'operation', true, 1),
(1010402, '供应商添加', NULL, NULL, 10104, NULL, 'asset:supplier:add', 'operation', true, 2),
(1010403, '供应商编辑', NULL, NULL, 10104, NULL, 'asset:supplier:edit', 'operation', true, 3),
(1010404, '供应商删除', NULL, NULL, 10104, NULL, 'asset:supplier:delete', 'operation', true, 4),
-- 物资列表操作
(1020101, '物资列表', NULL, NULL, 10201, NULL, 'material:list', 'operation', true, 1),
(1020102, '物资添加', NULL, NULL, 10201, NULL, 'material:add', 'operation', true, 2),
(1020103, '物资编辑', NULL, NULL, 10201, NULL, 'material:edit', 'operation', true, 3),
(1020104, '物资删除', NULL, NULL, 10201, NULL, 'material:delete', 'operation', true, 4),
-- 批次列表操作
(1020201, '批次列表', NULL, NULL, 10202, NULL, 'material:batch:list', 'operation', true, 1),
(1020202, '批次添加', NULL, NULL, 10202, NULL, 'material:batch:add', 'operation', true, 2),
(1020203, '批次编辑', NULL, NULL, 10202, NULL, 'material:batch:edit', 'operation', true, 3),
(1020204, '批次删除', NULL, NULL, 10202, NULL, 'material:batch:delete', 'operation', true, 4),
-- 物资入库操作
(1020301, '物资入库列表', NULL, NULL, 10203, NULL, 'material:in:list', 'operation', true, 1),
(1020302, '物资入库添加', NULL, NULL, 10203, NULL, 'material:in:add', 'operation', true, 2),
(1020303, '物资入库编辑', NULL, NULL, 10203, NULL, 'material:in:edit', 'operation', true, 3),
(1020304, '物资入库删除', NULL, NULL, 10203, NULL, 'material:in:delete', 'operation', true, 4),
-- 物资出库操作
(1020401, '物资出库列表', NULL, NULL, 10204, NULL, 'material:out:list', 'operation', true, 1),
(1020402, '物资出库添加', NULL, NULL, 10204, NULL, 'material:out:add', 'operation', true, 2),
(1020403, '物资出库编辑', NULL, NULL, 10204, NULL, 'material:out:edit', 'operation', true, 3),
(1020404, '物资出库删除', NULL, NULL, 10204, NULL, 'material:out:delete', 'operation', true, 4),
-- 物资调拨操作
(1020501, '物资调拨列表', NULL, NULL, 10205, NULL, 'material:transfer:list', 'operation', true, 1),
(1020502, '物资调拨添加', NULL, NULL, 10205, NULL, 'material:transfer:add', 'operation', true, 2),
(1020503, '物资调拨编辑', NULL, NULL, 10205, NULL, 'material:transfer:edit', 'operation', true, 3),
(1020504, '物资调拨删除', NULL, NULL, 10205, NULL, 'material:transfer:delete', 'operation', true, 4),
-- 物资盘点操作
(1020601, '物资盘点列表', NULL, NULL, 10206, NULL, 'material:inventory:list', 'operation', true, 1),
(1020602, '物资盘点添加', NULL, NULL, 10206, NULL, 'material:inventory:add', 'operation', true, 2),
(1020603, '物资盘点编辑', NULL, NULL, 10206, NULL, 'material:inventory:edit', 'operation', true, 3),
(1020604, '物资盘点删除', NULL, NULL, 10206, NULL, 'material:inventory:delete', 'operation', true, 4),
-- 物资分类操作
(1020701, '物资分类列表', NULL, NULL, 10207, NULL, 'material:category:list', 'operation', true, 1),
(1020702, '物资分类添加', NULL, NULL, 10207, NULL, 'material:category:add', 'operation', true, 2),
(1020703, '物资分类编辑', NULL, NULL, 10207, NULL, 'material:category:edit', 'operation', true, 3),
(1020704, '物资分类删除', NULL, NULL, 10207, NULL, 'material:category:delete', 'operation', true, 4),
-- 物资品类操作
(1021001, '物资品类列表', NULL, NULL, 10210, NULL, 'material:brand:list', 'operation', true, 1),
(1021002, '物资品类添加', NULL, NULL, 10210, NULL, 'material:brand:add', 'operation', true, 2),
(1021003, '物资品类编辑', NULL, NULL, 10210, NULL, 'material:brand:edit', 'operation', true, 3),
(1021004, '物资品类删除', NULL, NULL, 10210, NULL, 'material:brand:delete', 'operation', true, 4),
-- 物资仓库操作
(1020801, '物资仓库列表', NULL, NULL, 10208, NULL, 'material:warehouse:list', 'operation', true, 1),
(1020802, '物资仓库添加', NULL, NULL, 10208, NULL, 'material:warehouse:add', 'operation', true, 2),
(1020803, '物资仓库编辑', NULL, NULL, 10208, NULL, 'material:warehouse:edit', 'operation', true, 3),
(1020804, '物资仓库删除', NULL, NULL, 10208, NULL, 'material:warehouse:delete', 'operation', true, 4),
-- 物资管理操作
(1021201, '物资管理列表', NULL, NULL, 10212, NULL, 'material:list', 'operation', true, 1),
(1021202, '物资管理添加', NULL, NULL, 10212, NULL, 'material:add', 'operation', true, 2),
(1021203, '物资管理编辑', NULL, NULL, 10212, NULL, 'material:edit', 'operation', true, 3),
(1021204, '物资管理删除', NULL, NULL, 10212, NULL, 'material:delete', 'operation', true, 4),
-- 领用记录操作
(1020901, '领用记录列表', NULL, NULL, 10209, NULL, 'material:use:list', 'operation', true, 1),
(1020902, '领用记录添加', NULL, NULL, 10209, NULL, 'material:use:add', 'operation', true, 2),
(1020903, '领用记录编辑', NULL, NULL, 10209, NULL, 'material:use:edit', 'operation', true, 3),
(1020904, '领用记录删除', NULL, NULL, 10209, NULL, 'material:use:delete', 'operation', true, 4),
-- 分类统计操作
(1030101, '分类统计查看', NULL, NULL, 10301, NULL, 'asset:statistics:category:view', 'operation', true, 1),
-- 位置统计操作
(1030201, '位置统计查看', NULL, NULL, 10302, NULL, 'asset:statistics:location:view', 'operation', true, 1),
-- 部门资产统计操作
(1030301, '部门资产统计查看', NULL, NULL, 10303, NULL, 'asset:statistics:dept:view', 'operation', true, 1),
-- 员工资产统计操作
(1030401, '员工资产统计查看', NULL, NULL, 10304, NULL, 'asset:statistics:employee:view', 'operation', true, 1),
-- 库存统计操作
(1040101, '库存统计查看', NULL, NULL, 10401, NULL, 'material:statistics:stock:view', 'operation', true, 1),
-- 进出统计操作
(1040201, '进出统计查看', NULL, NULL, 10402, NULL, 'material:statistics:inout:view', 'operation', true, 1),
-- 领用统计操作
(1040301, '领用统计查看', NULL, NULL, 10403, NULL, 'material:statistics:use:view', 'operation', true, 1),
-- 我的资产操作
(1070101, '我的资产列表', NULL, NULL, 10701, NULL, 'asset:my:list', 'operation', true, 1),
-- 资产领用操作
(1070201, '资产领用列表', NULL, NULL, 10702, NULL, 'asset:apply:list', 'operation', true, 1),
(1070202, '资产领用添加', NULL, NULL, 10702, NULL, 'asset:apply:add', 'operation', true, 2),
(1070203, '资产领用编辑', NULL, NULL, 10702, NULL, 'asset:apply:edit', 'operation', true, 3),
(1070204, '资产领用删除', NULL, NULL, 10702, NULL, 'asset:apply:delete', 'operation', true, 4),
-- 资产退库操作
(1070301, '资产退库列表', NULL, NULL, 10703, NULL, 'asset:return:list', 'operation', true, 1),
(1070302, '资产退库添加', NULL, NULL, 10703, NULL, 'asset:return:add', 'operation', true, 2),
(1070303, '资产退库编辑', NULL, NULL, 10703, NULL, 'asset:return:edit', 'operation', true, 3),
(1070304, '资产退库删除', NULL, NULL, 10703, NULL, 'asset:return:delete', 'operation', true, 4),
-- 资产转移操作
(1070401, '资产转移列表', NULL, NULL, 10704, NULL, 'asset:transfer:list', 'operation', true, 1),
(1070402, '资产转移添加', NULL, NULL, 10704, NULL, 'asset:transfer:add', 'operation', true, 2),
(1070403, '资产转移编辑', NULL, NULL, 10704, NULL, 'asset:transfer:edit', 'operation', true, 3),
(1070404, '资产转移删除', NULL, NULL, 10704, NULL, 'asset:transfer:delete', 'operation', true, 4),
-- 资产报修操作
(1070501, '资产报修列表', NULL, NULL, 10705, NULL, 'asset:repair:list', 'operation', true, 1),
(1070502, '资产报修添加', NULL, NULL, 10705, NULL, 'asset:repair:add', 'operation', true, 2),
(1070503, '资产报修编辑', NULL, NULL, 10705, NULL, 'asset:repair:edit', 'operation', true, 3),
(1070504, '资产报修删除', NULL, NULL, 10705, NULL, 'asset:repair:delete', 'operation', true, 4);

-- 为admin角色分配权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
-- 资产管理权限
(1, 10000), 
(1, 10001), (1, 1000101), (1, 1000102), (1, 1000103), (1, 1000104),
(1, 10002), (1, 1000201), (1, 1000202), (1, 1000203), (1, 1000204),
(1, 10003), (1, 1000301), (1, 1000302), (1, 1000303), (1, 1000304), (1, 1000305), (1, 1000306), (1, 1000307),
(1, 10004), (1, 1000401), (1, 1000402), (1, 1000403), (1, 1000404), (1, 1000405), (1, 1000406), (1, 1000407),
(1, 10005), (1, 1000501), (1, 1000502),
(1, 10006), (1, 1000601), (1, 1000602),
(1, 10007), (1, 1000701), (1, 1000702),
(1, 10008), (1, 1000801), (1, 1000802), (1, 1000803), (1, 1000804),
(1, 10009), (1, 1000901), (1, 1000902), (1, 1000903), (1, 1000904),
(1, 10010), (1, 1001001), (1, 1001002), (1, 1001003), (1, 1001004),
(1, 10011), (1, 1001101), 
(1, 10012), (1, 1001201), (1, 1001202), (1, 1001203), (1, 1001204),
-- 采购管理权限
(1, 10100), 
(1, 10101), (1, 1010101), (1, 1010102), (1, 1010103), (1, 1010104), 
(1, 10102), (1, 1010201), (1, 1010202), (1, 1010203), (1, 1010204),
(1, 10103), (1, 1010301), (1, 1010302), (1, 1010303), (1, 1010304),
(1, 10104), (1, 1010401), (1, 1010402), (1, 1010403), (1, 1010404),
-- 物资管理权限
(1, 10200), 
(1, 10201), (1, 1020101), (1, 1020102), (1, 1020103), (1, 1020104), 
(1, 10202), (1, 1020201), (1, 1020202), (1, 1020203), (1, 1020204), 
(1, 10203), (1, 1020301), (1, 1020302), (1, 1020303), (1, 1020304), 
(1, 10204), (1, 1020401), (1, 1020402), (1, 1020403), (1, 1020404), 
(1, 10205), (1, 1020501), (1, 1020502), (1, 1020503), (1, 1020504), 
(1, 10206), (1, 1020601), (1, 1020602), (1, 1020603), (1, 1020604), 
(1, 10207), (1, 1020701), (1, 1020702), (1, 1020703), (1, 1020704), 
(1, 10208), (1, 1020801), (1, 1020802), (1, 1020803), (1, 1020804), 
(1, 10209), (1, 1020901), (1, 1020902), (1, 1020903), (1, 1020904),
(1, 10210), (1, 1021001), (1, 1021002), (1, 1021003), (1, 1021004),
(1, 10212), (1, 1021201), (1, 1021202), (1, 1021203), (1, 1021204),
-- 资产统计权限
(1, 10300), 
(1, 10301), (1, 1030101), 
(1, 10302), (1, 1030201), 
(1, 10303), (1, 1030301), 
(1, 10304), (1, 1030401),
-- 物资统计权限
(1, 10400), 
(1, 10401), (1, 1040101), 
(1, 10402), (1, 1040201), 
(1, 10403), (1, 1040301),
-- 维保管理权限
(1, 10500),
-- 数据大屏权限
(1, 10600),
-- 个人中心权限
(1, 10700), 
(1, 10701), (1, 1070101), 
(1, 10702), (1, 1070201), (1, 1070202), (1, 1070203), (1, 1070204),
(1, 10703), (1, 1070301), (1, 1070302), (1, 1070303), (1, 1070304),
(1, 10704), (1, 1070401), (1, 1070402), (1, 1070403), (1, 1070404),
(1, 10705), (1, 1070501), (1, 1070502), (1, 1070503), (1, 1070504);

-- 物资分类表
CREATE TABLE IF NOT EXISTS `material_category` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资分类表';

-- 插入物资分类示例数据
-- 插入父节点
INSERT INTO material_category (id, name, parent_id, path, level, remark, status, create_time, update_time) VALUES
(1, '办公用品', 0, '1', 1, '办公用品分类', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(2, '维修耗材', 0, '2', 1, '维修耗材分类', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 插入子节点
-- 办公用品子节点
INSERT INTO material_category (id, name, parent_id, path, level, remark, status, create_time, update_time) VALUES
(3, '笔记本', 1, '1,3', 2, '各类笔记本', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(4, '打印纸', 1, '1,4', 2, 'A4打印纸等', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(5, '写字笔', 1, '1,5', 2, '签字笔、圆珠笔等', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 维修耗材子节点
INSERT INTO material_category (id, name, parent_id, path, level, remark, status, create_time, update_time) VALUES
(6, '配件', 2, '2,6', 2, '各类维修配件', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 物资品类表
CREATE TABLE IF NOT EXISTS `material_brand` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `category_id` BIGINT NOT NULL COMMENT '物资分类ID',
  `name` VARCHAR(255) NOT NULL COMMENT '品类名称',
  `sn` VARCHAR(255) COMMENT '商品条码',
  `specification` VARCHAR(255) COMMENT '规格型号',
  `unit` VARCHAR(50) COMMENT '单位',
  `pack_unit` VARCHAR(50) COMMENT '包装单位',
  `pack_quantity` INT COMMENT '包装数量',
  `pack_barcode` VARCHAR(255) COMMENT '包装条码',
  `brand` VARCHAR(255) COMMENT '品牌',
  `price` DECIMAL(10,2) COMMENT '价格',
  `remark` VARCHAR(500) COMMENT '备注',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `create_time` BIGINT NOT NULL COMMENT '创建时间',
  `update_time` BIGINT NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资品类表';

-- 插入物资品类示例数据
INSERT INTO material_brand (id, category_id, name, sn, specification, unit, pack_unit, pack_quantity, pack_barcode, brand, price, remark, status, create_time, update_time) VALUES
(1, 4, '晨光打印纸', '6921734900011', 'A4-500页', '包', '箱', 10, '6921734900028', '得力', 25.00, '得力办公用品', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(2, 5, '晨光写字笔', '6923566500012', '0.5mm', '支', '盒', 20, '6923566500029', '晨光', 2.00, '晨光文具', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(3, 6, '维修配件套装', '6921388000013', '套装', '套', '箱', 5, '6921388000020', '得力', 150.00, '维修配件', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 物资仓库表
CREATE TABLE IF NOT EXISTS `material_warehouse` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` VARCHAR(255) NOT NULL COMMENT '仓库名称',
  `code` VARCHAR(50) NOT NULL COMMENT '仓库编码',
  `address` VARCHAR(500) COMMENT '仓库地址',
  `contact_person` VARCHAR(100) COMMENT '联系人',
  `contact_phone` VARCHAR(50) COMMENT '联系电话',
  `remark` VARCHAR(500) COMMENT '备注',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `create_time` BIGINT NOT NULL COMMENT '创建时间',
  `update_time` BIGINT NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资仓库表';

-- 物资库位表
CREATE TABLE IF NOT EXISTS `material_location` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `warehouse_id` BIGINT NOT NULL COMMENT '仓库ID',
  `name` VARCHAR(255) NOT NULL COMMENT '库位名称',
  `code` VARCHAR(50) NOT NULL COMMENT '库位编码',
  `remark` VARCHAR(500) COMMENT '备注',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `create_time` BIGINT NOT NULL COMMENT '创建时间',
  `update_time` BIGINT NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_warehouse_code` (`warehouse_id`, `code`),
  INDEX `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资库位表';

-- 插入物资仓库示例数据
INSERT INTO material_warehouse (id, name, code, address, contact_person, contact_phone, remark, status, create_time, update_time) VALUES
(1, '主仓库', 'WH001', '北京市朝阳区建国路88号', '张三', '13800138000', '主仓库', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(2, '分仓库', 'WH002', '上海市浦东新区张江高科技园区', '李四', '13900139000', '分仓库', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 插入物资库位示例数据
INSERT INTO material_location (id, warehouse_id, name, code, remark, status, create_time, update_time) VALUES
(1, 1, 'A区', 'A001', 'A区库位', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(2, 1, 'B区', 'B001', 'B区库位', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(3, 2, 'C区', 'C001', 'C区库位', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(4, 2, 'D区', 'D001', 'D区库位', 1, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 物资主表
CREATE TABLE IF NOT EXISTS `material` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` VARCHAR(255) NOT NULL COMMENT '物资名称',
  `code` VARCHAR(50) NOT NULL COMMENT '物资编码',
  `category_id` BIGINT NOT NULL COMMENT '物资分类ID',
  `brand_id` BIGINT NOT NULL COMMENT '物资品类ID',
  `total_quantity` DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '总数量',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` BIGINT NOT NULL COMMENT '创建时间',
  `update_time` BIGINT NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  INDEX `idx_category_id` (`category_id`),
  INDEX `idx_brand_id` (`brand_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资主表';

-- 物资明细表
CREATE TABLE IF NOT EXISTS `material_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `material_id` BIGINT NOT NULL COMMENT '物资ID',
  `brand_id` BIGINT NOT NULL COMMENT '物资品类ID',
  `warehouse_id` BIGINT NOT NULL COMMENT '仓库ID',
  `location_id` BIGINT NOT NULL COMMENT '库位ID',
  `quantity` DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '数量',
  `unit` VARCHAR(50) NOT NULL COMMENT '单位',
  `batch_no` VARCHAR(100) COMMENT '批次号',
  `expire_date` BIGINT COMMENT '过期日期',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` BIGINT NOT NULL COMMENT '创建时间',
  `update_time` BIGINT NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_material_id` (`material_id`),
  INDEX `idx_brand_id` (`brand_id`),
  INDEX `idx_warehouse_id` (`warehouse_id`),
  INDEX `idx_location_id` (`location_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资明细表';

-- 插入物资主表示例数据
INSERT INTO material (id, name, code, category_id, brand_id, total_quantity, status, remark, create_time, update_time) VALUES
(1, '打印纸', 'MAT001', 4, 1, 100.00, 1, '打印纸物资', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(2, '写字笔', 'MAT002', 5, 2, 200.00, 1, '写字笔物资', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(3, '维修配件', 'MAT003', 6, 3, 50.00, 1, '维修配件物资', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 插入物资明细示例数据
INSERT INTO material_detail (id, material_id, brand_id, warehouse_id, location_id, quantity, unit, batch_no, expire_date, status, remark, create_time, update_time) VALUES
(1, 1, 1, 1, 1, 50.00, '包', 'BATCH001', NULL, 1, '主仓库A区打印纸', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(2, 1, 1, 1, 2, 30.00, '包', 'BATCH002', NULL, 1, '主仓库B区打印纸', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(3, 1, 1, 2, 3, 20.00, '包', 'BATCH003', NULL, 1, '分仓库C区打印纸', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(4, 2, 2, 1, 1, 100.00, '支', 'BATCH004', NULL, 1, '主仓库A区写字笔', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(5, 2, 2, 1, 2, 60.00, '支', 'BATCH005', NULL, 1, '主仓库B区写字笔', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(6, 2, 2, 2, 3, 40.00, '支', 'BATCH006', NULL, 1, '分仓库C区写字笔', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(7, 3, 3, 1, 1, 30.00, '套', 'BATCH007', NULL, 1, '主仓库A区维修配件', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(8, 3, 3, 2, 3, 20.00, '套', 'BATCH008', NULL, 1, '分仓库C区维修配件', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 插入系统配置
INSERT INTO sys_config (config_key, value, description, create_time, update_time) VALUES
('system.print_service_url', 'http://127.0.0.1:8000/api', '打印服务URL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
