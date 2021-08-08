package de.polocloud.permission.api.database.adapter.executes.pull;

import de.polocloud.permission.api.database.adapter.DatabaseExecutor;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseGet {

    public List<Object> getListFromTable(String tableName, String column) {
        List<Object> list = DatabaseExecutor.getDatabaseExecutor().executeQuery("SELECT * FROM " + tableName, resultSet -> {
            List<Object> content = new ArrayList<>();
            while (resultSet.next()) content.add(resultSet.getString(column));
            return content;
        }, new ArrayList<>());
        return !list.isEmpty() ? list : null;
    }

    public Object getFromTable(String tableName, String column, String value, String neededColumn) {
        return DatabaseExecutor.getDatabaseExecutor().executeQuery("SELECT * FROM " + tableName + " WHERE " + column + "='" + value + "'", resultSet -> {
            if (resultSet.next()) {
                return resultSet.getString(neededColumn);
            }
            return "not found";
        }, "null");
    }

    public ResultSet getResultsAllFromTable(String tableName, String column, String value) {
        return DatabaseExecutor.getDatabaseExecutor().executeQuery("SELECT * FROM " + tableName + " WHERE " + column + "='" + value + "'",
            resultSet -> resultSet, null);
    }

    public int getTablePosition(String table, String type, String key, String searchBY) {
        AtomicInteger count = new AtomicInteger();
        Number place = DatabaseExecutor.getDatabaseExecutor().executeQuery("SELECT * FROM " + table + " ORDER BY " + searchBY + " DESC", resultSet -> {
            while (resultSet.next()) {
                if (resultSet.getString(type).equalsIgnoreCase(key)) {
                    count.set(resultSet.getRow());
                    return count;
                }
            }
            return -1;
        }, -1);
        return place.intValue();
    }

    public Map<Integer, String> sortByObject(String table, String key, String type, int max) {
        AtomicInteger count = new AtomicInteger(1);
        Map<Integer, String> list = new ConcurrentHashMap<>();
        DatabaseExecutor.getDatabaseExecutor().executeQuery("SELECT * FROM " + table + " ORDER BY " + type + " DESC LIMIT " + max, resultSet -> {
            while (resultSet.next()) list.put(count.getAndIncrement(), resultSet.getString(key));
            return list;
        }, list);
        return list;
    }

}
