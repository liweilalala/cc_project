package com.example.dbquery;

import com.example.dbquery.model.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseExecutor {
    private String host;
    private int port;
    private String user;
    private String password;
    private String database;

    public DatabaseExecutor(String host, int port, String user, String password, String database) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
    }

    public Result execute(String sql, List<Object> params) {
        Result result = new Result();
        result.setDatabase(database);

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&serverTimezone=UTC";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            List<String> columns = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columns.add(metaData.getColumnName(i));
            }
            result.setColumns(columns);

            List<Map<String, Object>> rows = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    if (value instanceof byte[]) {
                        row.put(columns.get(i - 1), new String((byte[]) value));
                    } else {
                        row.put(columns.get(i - 1), value);
                    }
                }
                rows.add(row);
            }

            result.setSuccess(true);
            result.setTable(sql.contains("FROM") ? sql.split("FROM")[1].trim().split(" ")[0] : "");
            result.setRowCount(rows.size());
            result.setRows(rows);

        } catch (SQLException e) {
            result.setSuccess(false);
            result.setError("查询执行失败: " + e.getMessage());
        }

        return result;
    }
}