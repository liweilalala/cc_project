---
name: mysql-query
description: MySQL 数据库查询工具。当用户提到"查询数据库"、"查 MySQL"、"执行 SQL"、"数据库操作"、输入表名/数据库名进行查询、或以 JSON 格式传入查询条件时触发。用于连接 MySQL 数据库、执行 SELECT 查询并返回结构化结果。
---

# MySQL Query Skill

使用配置文件连接 MySQL 数据库，执行只读的 SELECT 查询操作。

## 工作流程

### 1. 加载数据库配置

从配置文件加载数据库连接参数。配置文件路径通过 `db_config_path` 参数传入，默认路径为 `~/.mysql_config.json`。

配置文件格式 (JSON):
```json
{
  "host": "localhost",
  "port": 3306,
  "user": "root",
  "password": "your_password",
  "database": "default_database"
}
```

### 2. 执行查询

使用 `mysql-query` 脚本执行查询：

```bash
python ~/.claude/skills/mysql-query/scripts/mysql_query.py \
  --config <db_config_path> \
  --database <database_name> \
  --table <table_name> \
  --conditions '<json_conditions>'
```

### 3. JSON 查询条件格式

查询条件为 JSON 格式，支持以下字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| `columns` | array | 要查询的列名，默认 `["*"]` |
| `where` | object | WHERE 条件，键为列名，值为条件值 |
| `order_by` | string | 排序字段 |
| `order_dir` | string | 排序方向，`ASC` 或 `DESC`，默认 `ASC` |
| `limit` | integer | 返回行数限制 |

**示例查询条件：**
```json
{
  "columns": ["id", "username", "email"],
  "where": {
    "status": "active",
    "age >= ": 18
  },
  "order_by": "created_at",
  "order_dir": "DESC",
  "limit": 10
}
```

### 4. 返回结果格式

查询结果以 JSON 格式返回：

```json
{
  "success": true,
  "database": "my_database",
  "table": "users",
  "columns": ["id", "username", "email", "status", "age", "created_at"],
  "row_count": 3,
  "rows": [
    {"id": 1, "username": "alice", "email": "alice@example.com", "status": "active", "age": 25, "created_at": "2024-01-15 10:30:00"},
    {"id": 2, "username": "bob", "email": "bob@example.com", "status": "active", "age": 30, "created_at": "2024-01-16 14:20:00"},
    {"id": 3, "username": "charlie", "email": "charlie@example.com", "status": "active", "age": 28, "created_at": "2024-01-17 09:15:00"}
  ]
}
```

**错误响应：**
```json
{
  "success": false,
  "error": "Error message here"
}
```

## 使用示例

**用户输入：**
```
查询数据库 my_app，表名 users，查询条件：
{
  "columns": ["id", "username"],
  "where": {"status": "active"},
  "limit": 5
}
```

**执行步骤：**
1. 使用配置 `~/.mysql_config.json`，连接数据库 `my_app`
2. 执行 SQL: `SELECT id, username FROM users WHERE status = 'active' LIMIT 5`
3. 返回 JSON 结果

## 限制

- 仅支持 SELECT 查询，禁止执行 INSERT、UPDATE、DELETE 等写操作
- 查询结果自动限制最大返回 1000 行
- 连接超时时间为 30 秒

## 错误处理

| 错误类型 | 处理方式 |
|----------|----------|
| 配置文件不存在 | 返回错误，提示用户检查配置文件路径 |
| 连接失败 | 返回错误，包含连接错误信息 |
| SQL 语法错误 | 返回错误，提示 SQL 错误详情 |
| 表/列不存在 | 返回错误，提示相关错误信息 |