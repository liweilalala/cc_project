package com.example.dbquery;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.FileReader;
import java.io.IOException;

public class ConfigLoader {
    private String host;
    private int port;
    private String user;
    private String password;
    private String database;

    public void load(String configPath) throws IOException {
        Gson gson = new Gson();
        JsonObject config = gson.fromJson(new FileReader(configPath), JsonObject.class);
        this.host = config.get("host").getAsString();
        this.port = config.get("port").getAsInt();
        this.user = config.get("user").getAsString();
        this.password = config.get("password").getAsString();
        this.database = config.get("database").getAsString();
    }

    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getUser() { return user; }
    public String getPassword() { return password; }
    public String getDatabase() { return database; }
}