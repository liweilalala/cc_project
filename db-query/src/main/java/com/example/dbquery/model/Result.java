package com.example.dbquery.model;

public class Result {
    private boolean success;
    private String database;
    private String table;
    private java.util.List<String> columns;
    private int rowCount;
    private java.util.List<java.util.Map<String, Object>> rows;
    private String error;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getDatabase() { return database; }
    public void setDatabase(String database) { this.database = database; }
    public String getTable() { return table; }
    public void setTable(String table) { this.table = table; }
    public java.util.List<String> getColumns() { return columns; }
    public void setColumns(java.util.List<String> columns) { this.columns = columns; }
    public int getRowCount() { return rowCount; }
    public void setRowCount(int rowCount) { this.rowCount = rowCount; }
    public java.util.List<java.util.Map<String, Object>> getRows() { return rows; }
    public void setRows(java.util.List<java.util.Map<String, Object>> rows) { this.rows = rows; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}