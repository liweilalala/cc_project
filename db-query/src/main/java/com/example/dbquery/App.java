package com.example.dbquery;

import com.example.dbquery.model.Result;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;

public class App {
    public static void main(String[] args) {
        String configPath = System.getProperty("user.home") + "/.db_config.json";
        String database = null;
        String table = null;
        String conditionsJson = null;

        for (int i = 0; i < args.length; i++) {
            if ("--config".equals(args[i]) && i + 1 < args.length) {
                configPath = args[++i];
            } else if ("--database".equals(args[i]) && i + 1 < args.length) {
                database = args[++i];
            } else if ("--table".equals(args[i]) && i + 1 < args.length) {
                table = args[++i];
            } else if ("--conditions".equals(args[i]) && i + 1 < args.length) {
                conditionsJson = args[++i];
            }
        }

        if (database == null || table == null || conditionsJson == null) {
            System.err.println("Usage: java -jar db-query.jar --config <path> --database <db> --table <table> --conditions '<json>'");
            System.exit(1);
        }

        try {
            ConfigLoader configLoader = new ConfigLoader();
            configLoader.load(configPath);

            QueryBuilder queryBuilder = new QueryBuilder();
            JsonObject conditions = JsonParser.parseString(conditionsJson).getAsJsonObject();
            String sql = queryBuilder.build(conditions, table);
            java.util.List<Object> params = queryBuilder.getParams(conditions);

            DatabaseExecutor executor = new DatabaseExecutor(
                configLoader.getHost(),
                configLoader.getPort(),
                configLoader.getUser(),
                configLoader.getPassword(),
                database
            );

            Result result = executor.execute(sql, params);

            com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(result));

        } catch (Exception e) {
            com.google.gson.Gson gson = new com.google.gson.Gson();
            Result errorResult = new Result();
            errorResult.setSuccess(false);
            errorResult.setError(e.getMessage());
            System.out.println(gson.toJson(errorResult));
            System.exit(1);
        }
    }
}