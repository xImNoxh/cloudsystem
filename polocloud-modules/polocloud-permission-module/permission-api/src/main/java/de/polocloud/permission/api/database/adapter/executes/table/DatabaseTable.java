package de.polocloud.permission.api.database.adapter.executes.table;

import de.polocloud.permission.api.database.adapter.DataType;
import de.polocloud.permission.api.database.adapter.DatabaseExecutor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseTable {

    public Map<String, DataType> getTableInformation(String[] key, DataType[] types) {
        Map<String, DataType> content = new ConcurrentHashMap<>();
        for (int i = 0; i < key.length; i++) content.put(key[i], types[i]);
        return content;
    }

    public void createTable(String tableName, Map<String, DataType> content) {
        StringBuilder update = new StringBuilder("CREATE TABLE IF NOT EXISTS `").append(tableName).append("` (`");
        int count = 0;
        for (String key : content.keySet()) {
            update.append(key).append("` ").append(content.get(key).getSqlTag()).append(count + 1 >= content.size() ? ")" : ", `");
            count++;
        }

        CompletableFuture.supplyAsync(() -> DatabaseExecutor.getDatabaseExecutor().executeUpdate(update.toString()));
    }

}
