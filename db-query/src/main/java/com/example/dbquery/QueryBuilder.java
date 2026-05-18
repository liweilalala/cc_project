package com.example.dbquery;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {
    public String build(JsonObject conditions, String table) {
        JsonArray columnsArr = conditions.has("columns")
            ? conditions.getAsJsonArray("columns")
            : null;

        String colStr;
        if (columnsArr == null || columnsArr.size() == 0) {
            colStr = "*";
        } else {
            List<String> cols = new ArrayList<>();
            for (int i = 0; i < columnsArr.size(); i++) {
                cols.add("`" + columnsArr.get(i).getAsString() + "`");
            }
            colStr = String.join(", ", cols);
        }

        StringBuilder sql = new StringBuilder("SELECT " + colStr + " FROM `" + table + "`");

        if (conditions.has("where")) {
            JsonObject where = conditions.getAsJsonObject("where");
            List<String> keys = new ArrayList<>(where.keySet());
            if (!keys.isEmpty()) {
                List<String> conditionsList = new ArrayList<>();
                for (String key : keys) {
                    conditionsList.add("`" + key + "` = ?");
                }
                sql.append(" WHERE ").append(String.join(" AND ", conditionsList));
            }
        }

        if (conditions.has("limit")) {
            int limit = conditions.get("limit").getAsInt();
            sql.append(" LIMIT ").append(Math.min(limit, 1000));
        }

        return sql.toString();
    }

    public List<Object> getParams(JsonObject conditions) {
        List<Object> params = new ArrayList<>();
        if (conditions.has("where")) {
            JsonObject where = conditions.getAsJsonObject("where");
            for (String key : where.keySet()) {
                params.add(where.get(key).getAsString());
            }
        }
        return params;
    }
}