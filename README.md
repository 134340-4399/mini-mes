# mini-MES

一个以真实产线视角构建的 MES（制造执行系统）学习项目，围绕 **工单 → 报工 → 物料领用 → 设备维护** 这条产线主线展开。

## 技术栈

- Java 21 + Spring Boot 3.4
- MyBatis-Plus 3.5.9 + MySQL 9.5
- JWT（jjwt 0.12.5）鉴权 + BCrypt 密码哈希
- DeepSeek API（含 Agent Function Calling）

## 已实现功能

| 模块 | 说明 |
|---|---|
| 用户 / 登录 | 注册、登录、JWT 鉴权，密码 BCrypt 存储 |
| 工单 | 创建、按产线查询、状态流转（待生产/生产中/已完成） |
| 报工 | 创建报工、按工单查询、产线聚合视图（跨表 JOIN） |
| 设备 | 完整增删改查、按产线筛选 |
| 维护记录 | 新增、查询 |
| 物料 | 入库（事务原子）、领料（并发安全的原子扣减） |
| AI 问答 | 普通对话 + Agent Function Calling（调用真实报工数据回答） |

## 亮点

- **并发控制**：领料扣库存用 `UPDATE material SET stock = stock - #{quantity} WHERE id = #{id} AND stock >= #{quantity}`，判断与扣减焊在同一条 SQL 里，靠影响行数区分"库存不足"，防止并发超卖。
- **事务原子性**：入库 / 领料都是"写记录 + 改库存"两步在一个 `@Transactional` 里完成。
- **操作人溯源**：报工 / 领料的操作人从登录态（JWT → UserContext）自动取，不信任前端传值。

## 快速开始

### 1. 准备数据库

```sql
CREATE DATABASE factorydata DEFAULT CHARSET utf8mb4;
```

建表 SQL 见 `sql/` 目录（当前含 `material.sql`，其余表结构见 `src/main/resources/knowledge/mini-mes-知识库.md` 的字段说明）。

### 2. 配置私密信息

项目不提交真实密钥。复制以下内容到 `src/main/resources/application-secret.yml`（已被 `.gitignore` 排除）：

```yaml
DB_USERNAME: root
DB_PASSWORD: "你的数据库密码"
JWT_SECRET: "你的 JWT 密钥（任意长度随机串）"
DEEPSEEK_API_KEY: "你的 DeepSeek API Key"
```

### 3. 启动

```bash
mvn spring-boot:run
```

或 IDE 直接运行 `MiniMesApplication`，默认端口 8080。

### 4. 接口文档

完整字段字典与接口列表见 [`src/main/resources/knowledge/mini-mes-知识库.md`](src/main/resources/knowledge/mini-mes-知识库.md)。

所有接口统一返回 `Result<T>`：`code`（200 成功 / 400 业务失败 / 401 未登录）、`message`、`data`。除登录 / 注册外，均需在请求头带 `Authorization: Bearer <token>`。

## 待办

- [ ] RAG：当前 AI 问答为"整本知识库塞进 prompt"，下一步做向量化检索（DeepSeek 目前无 embedding 接口，需选型外部 embedding）。
- [ ] 前端：后端 API 已齐，Vue 前端未接。
