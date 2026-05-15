#!/usr/bin/env python3
"""
MySQL Query Tool - 执行只读 SELECT 查询
"""

import argparse
import json
import sys
import os

try:
    import pymysql
except ImportError:
    print(json.dumps({"success": False, "error": "请先安装 pymysql: pip install pymysql"}))
    sys.exit(1)


def load_config(config_path: str) -> dict:
    """加载数据库配置"""
    if not os.path.exists(config_path):
        raise FileNotFoundError(f"配置文件不存在: {config_path}")

    with open(config_path, 'r', encoding='utf-8') as f:
        return json.load(f)


def build_where_clause(where_dict: dict) -> tuple:
    """构建 WHERE 子句"""
    if not where_dict:
        return "", []

    conditions = []
    params = []

    for key, value in where_dict.items():
        if key.endswith(" >="):
            col = key.rstrip(" >=")
            conditions.append(f"{col} >= %s")
        elif key.endswith(" <="):
            col = key.rstrip(" <=")
            conditions.append(f"{col} <= %s")
        elif key.endswith(" !="):
            col = key.rstrip(" !=")
            conditions.append(f"{col} != %s")
        elif key.endswith(" >"):
            col = key.rstrip(" >")
            conditions.append(f"{col} > %s")
        elif key.endswith(" <"):
            col = key.rstrip(" <")
            conditions.append(f"{col} < %s")
        elif key.endswith(" LIKE"):
            col = key.rstrip(" LIKE")
            conditions.append(f"{col} LIKE %s")
        else:
            conditions.append(f"{key} = %s")

        if isinstance(value, str) and "%" in value:
            params.append(value)
        elif isinstance(value, (int, float)):
            params.append(value)
        else:
            params.append(str(value))

    return " WHERE " + " AND ".join(conditions), params


def build_query(database: str, table: str, conditions: dict) -> tuple:
    """构建 SQL 查询"""
    columns = conditions.get("columns", ["*"])
    if columns == ["*"]:
        col_str = "*"
    else:
        col_str = ", ".join(f"`{c}`" for c in columns)

    sql = f"SELECT {col_str} FROM `{database}`.`{table}`"

    where_clause, params = build_where_clause(conditions.get("where", {}))
    sql += where_clause

    order_by = conditions.get("order_by")
    if order_by:
        sql += f" ORDER BY `{order_by}`"
        order_dir = conditions.get("order_dir", "ASC").upper()
        if order_dir in ("ASC", "DESC"):
            sql += f" {order_dir}"

    limit = conditions.get("limit", 1000)
    sql += f" LIMIT {min(limit, 1000)}"

    return sql, params


def execute_query(config: dict, database: str, table: str, conditions: dict) -> dict:
    """执行查询并返回结果"""
    try:
        connection = pymysql.connect(
            host=config.get("host", "localhost"),
            port=config.get("port", 3306),
            user=config.get("user", "root"),
            password=config.get("password", ""),
            database=database,
            charset="utf8mb4",
            cursorclass=pymysql.cursors.DictCursor,
            connect_timeout=30
        )
    except Exception as e:
        return {"success": False, "error": f"连接数据库失败: {str(e)}"}

    try:
        with connection.cursor() as cursor:
            sql, params = build_query(database, table, conditions)
            cursor.execute(sql, params)

            columns = [desc[0] for desc in cursor.description] if cursor.description else []
            rows = cursor.fetchall()

            for row in rows:
                for key, value in row.items():
                    if isinstance(value, bytes):
                        row[key] = value.decode("utf-8", errors="replace")
                    elif hasattr(value, 'isoformat'):
                        row[key] = value.isoformat()

            return {
                "success": True,
                "database": database,
                "table": table,
                "columns": columns,
                "row_count": len(rows),
                "rows": rows
            }
    except Exception as e:
        return {"success": False, "error": f"查询执行失败: {str(e)}"}
    finally:
        connection.close()


def main():
    parser = argparse.ArgumentParser(description="MySQL Query Tool")
    parser.add_argument("--config", default=os.path.expanduser("~/.mysql_config.json"),
                        help="数据库配置文件路径")
    parser.add_argument("--database", required=True, help="数据库名")
    parser.add_argument("--table", required=True, help="表名")
    parser.add_argument("--conditions", required=True, help="JSON 格式的查询条件")

    args = parser.parse_args()

    try:
        config = load_config(args.config)
    except FileNotFoundError as e:
        print(json.dumps({"success": False, "error": str(e)}))
        sys.exit(1)

    try:
        conditions = json.loads(args.conditions)
    except json.JSONDecodeError as e:
        print(json.dumps({"success": False, "error": f"查询条件 JSON 格式错误: {str(e)}"}))
        sys.exit(1)

    result = execute_query(config, args.database, args.table, conditions)
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()