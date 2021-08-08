package de.polocloud.permission.api.database.adapter.executes;

import de.polocloud.permission.api.database.adapter.DatabaseExecutor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DatabaseRemove {

    public void removeFromTable(String table, String column, String key) {
        DatabaseExecutor.getDatabaseExecutor().executeUpdate("DELETE FROM " + table + " WHERE " + column + "='" + key + "'");
    }

    public void removeMoreFromTable(String table, List<String> keys, List<String> values) {
        StringBuilder queryBuilder = new StringBuilder().append("DELETE FROM ").append(table).append(" WHERE ");
        for (int i = 0; i < keys.size(); ++i)
            queryBuilder.append(keys.get(i)).append("='").append(values.get(i)).append("'").append(i + 1 >= keys.size() ? "" : " AND ");
        CompletableFuture.supplyAsync(() -> DatabaseExecutor.getDatabaseExecutor().executeUpdate(queryBuilder.toString()));
    }

    public void removeAllFromTable(String table, String column, String value) {
        CompletableFuture.supplyAsync(() -> DatabaseExecutor.getDatabaseExecutor().executeUpdate("DELETE FROM " + table + " WHERE " + column + "='" + value + "'"));
    }


}
