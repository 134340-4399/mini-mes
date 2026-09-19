# mini-MES 系统知识库

> 本文件是 mini-MES 项目的接口文档 + 字段字典 + 业务规则，供 RAG（检索增强生成）使用。
> 每个 `##` 一节是一个独立的知识块，检索时按节切分。

## 项目概述

mini-MES 是一个制造执行系统（MES）的学习项目，技术栈为 Java + Spring Boot + MyBatis-Plus + MySQL + Vue。
系统围绕"工单 → 报工 → 物料领用 → 设备维护"这条产线主线展开。
所有接口统一返回 `Result<T>` 结构：`code`（200 成功 / 400 业务失败 / 401 未登录）、`message`、`data`。
所有接口前缀为 `/api`，除登录和注册外都需要 JWT 鉴权（请求头 `Authorization: Bearer <token>`）。

## 工单（work_order）字段与接口

工单表 `work_order` 字段：
- `id`：主键，自增
- `orderNo`：工单号，唯一（有唯一索引 uk_order_no）
- `productModel`：产品型号
- `quantity`：计划数量
- `lineName`：产线名称
- `status`：状态，取值 待生产 / 生产中 / 已完成
- `createTime` / `updateTime`：创建/更新时间

工单相关接口：
- `GET /api/work-orders`：查询工单列表，可选参数 `line`（按产线筛选），不传则查全部
- `POST /api/work-orders`：创建工单，请求体 JSON：`orderNo`、`productModel`、`quantity`、`lineName`
- `PUT /api/work-orders/{id}/status?status=生产中`：更新工单状态

## 报工（production_report）字段与接口

报工表 `production_report` 字段：
- `id`：主键，自增
- `workOrderId`：外键，关联工单主键 id
- `totalProducts`：总产量
- `qualifiedQuantity`：合格数
- `scrappedQuantity`：报废数
- `reason`：报废原因，可选
- `username`：报工人，从登录态（JWT）自动取
- `reportTime`：报工时间

报工相关接口：
- `POST /api/production-reports`：创建报工，参数用 query 传：`orderNo`（工单号）、`totalProducts`、`qualifiedQuantity`、`scrappedQuantity`、`reason`（可选）
- `GET /api/production-reports?orderNo=xxx`：按工单号查询该工单的所有报工记录
- `GET /api/production-reports/line-summary`：产线聚合视图，按产线汇总报工总数、合格总数、报废总数（跨表 JOIN 工单拿产线）

## 设备（equipment）字段与接口

设备表 `equipment` 字段：
- `id`：主键，自增
- `equipmentCode`：设备编码
- `equipmentName`：设备名称
- `equipmentModel`：设备型号
- `productionLine`：所属产线
- `equipmentStatus`：设备状态，默认"停机"
- `createTime`：创建时间

设备相关接口（完整增删改查）：
- `GET /api/equipment`：查询设备列表，可选参数 `line`（按产线筛选）
- `GET /api/equipment/{id}`：按主键查单个设备
- `POST /api/equipment`：新增设备，请求体 JSON：`equipmentCode`、`equipmentName`、`equipmentModel`、`productionLine`
- `PUT /api/equipment/{id}`：修改设备，请求体同上
- `DELETE /api/equipment/{id}`：按主键删除设备

## 设备维护（maintenance_record）字段与接口

维护记录表 `maintenance_record` 字段：
- `id`：主键，自增
- `equipmentCode`：设备编码（外键存编码，不用主键 id）
- `maintenanceType`：维护类型
- `faultDescription`：故障描述
- `maintenanceTime`：维护时间
- `cost`：维护成本，类型 BigDecimal
- `technician`：维修人员
- `result`：维修结果

维护相关接口：
- `POST /api/maintenance-records`：新增维护记录，请求体 JSON：`equipmentCode`、`maintenanceType`、`faultDescription`、`cost`、`technician`、`result`
- `GET /api/maintenance-records`：查询全部维护记录

## 物料（material）字段

物料表 `material` 字段：
- `id`：主键，自增
- `materialCode`：物料编码，唯一（有唯一索引 uk_material_code）
- `materialName`：物料名称
- `unit`：计量单位
- `stock`：当前库存，默认 0
- `createTime`：创建时间

注意：物料表本身没有对外增删改查接口，物料数据直接维护在数据库里。物料的操作通过下面的"入库"和"领料"两个接口完成。

## 物料入库（material_stock_in）接口

入库记录表 `material_stock_in` 字段：`id`、`materialId`（外键，物料主键 id）、`quantity`（入库数量）、`username`、`stockInTime`。

入库接口：
- `POST /api/material-stock-in`：物料入库，请求体 JSON：`materialId`（物料主键 id）、`quantity`（入库数量）
- 业务逻辑：入库 = 增加对应物料的 `stock` + 写一条入库记录，两步在同一个事务（@Transactional）里原子完成，任一步失败整体回滚。

## 物料领料（material_issue）接口

领料记录表 `material_issue` 字段：`id`、`workOrderId`（外键，工单主键 id）、`materialId`（外键，物料主键 id）、`quantity`（领料数量）、`username`、`issueTime`。

领料接口：
- `POST /api/issue`：物料领料，请求体 JSON：`orderNo`（工单号）、`materialId`（物料主键 id）、`quantity`（领料数量）
- 业务规则（并发控制）：扣减库存用原子 UPDATE，即 `UPDATE material SET stock = stock - #{quantity} WHERE id = #{materialId} AND stock >= #{quantity}`，判断和扣减焊在同一条 SQL 里，影响行数为 0 即"库存不足"。这样防止并发下的超卖（丢失更新）。
- 领料 = 校验工单存在 + 扣减库存 + 写领料记录，整体在一个事务里。顺序：先 SELECT 查物料（区分"物料不存在"vs"库存不足"）→ 原子扣减 → 插入领料记录。

## 用户与登录（users）字段

用户表 `users` 字段：`id`、`username`、`password`（BCrypt 加密存储，不存明文）、`realName`、`phone`、`role`、`status`、`createTime`。

登录与鉴权：
- 注册 / 登录接口走 JWT：注册后登录，登录成功返回合法 JWT，密码用 BCrypt 哈希校验。
- 后续请求在请求头带 `Authorization: Bearer <token>`，由拦截器校验并解析出 username 注入到 UserContext，报工/领料等操作从 UserContext 取操作人，不需要前端传 username。

## AI 智能问答接口

- `POST /api/ai`：普通对话，请求体 JSON `{ "question": "..." }`，调用 DeepSeek 返回文本回答。
- `POST /api/ai/tool`：带工具（Agent Function Calling）的对话，模型可以调用 `queryReports(orderNo)` 工具查询指定工单的报工记录，再基于真实数据回答。
