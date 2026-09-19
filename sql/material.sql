-- 物料/领料模块建表 SQL
-- 已在 factorydata 库建好（9/8 前后 Navicat 手动建），此处归档
-- 字符集统一 utf8mb4_0900_ai_ci（对齐 MySQL 9.5，别踩 9/3 的 COLLATE 坑）

-- 物料主表
CREATE TABLE `material` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_code` varchar(20) NOT NULL COMMENT '物料编码',
  `material_name` varchar(50) NOT NULL COMMENT '物料名',
  `unit` varchar(10) NOT NULL COMMENT '单位',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存余额',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_material_code` (`material_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='物料主表';

-- 物料发放记录表（领料/出库）
CREATE TABLE `material_issue` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `work_order_id` bigint NOT NULL COMMENT '工单主表ID（关联工单主表id）',
  `material_id` bigint NOT NULL COMMENT '物料主表ID（关联material.id）',
  `quantity` int NOT NULL COMMENT '发放数量',
  `username` varchar(50) NOT NULL COMMENT '领用人/操作人',
  `issue_time` datetime NOT NULL COMMENT '发放时间',
  PRIMARY KEY (`id`),
  KEY `idx_work_order_id` (`work_order_id`),
  KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='物料发放记录表';
