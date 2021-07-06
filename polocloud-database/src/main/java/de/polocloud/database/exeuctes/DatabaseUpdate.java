package de.polocloud.database.exeuctes;

import de.polocloud.database.DatabaseExecutor;

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
