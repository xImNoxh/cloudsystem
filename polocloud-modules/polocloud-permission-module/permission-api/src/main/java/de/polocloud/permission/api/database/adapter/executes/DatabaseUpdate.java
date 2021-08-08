package de.polocloud.permission.api.database.adapter.executes;

import de.polocloud.permission.api.database.adapter.DatabaseExecutor;

import java.util.concurrent.CompletableFuture;

public class DatabaseUpdate {

    public void updateInTable(String table, String keyRow, String keyValue, String setRow, Object setValue) {
        CompletableFuture.supplyAsync(() -> DatabaseExecutor.getDatabaseExecutor().executeUpdate("UPDATE " + table +
            " SET " + setRow + "= '" + setValue + "' WHERE " + keyRow + "= '" + keyValue + "';"));
    }

    public void updateAllInTable(String table, String setRow, String setValue) {
        CompletableFuture.supplyAsync(() -> DatabaseExecutor.getDatabaseExecutor().executeUpdate("UPDATE " + table + " SET " + setRow + "='" + setValue + "'"));
    }

}
