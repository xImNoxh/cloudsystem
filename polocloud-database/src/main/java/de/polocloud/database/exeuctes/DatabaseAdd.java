package de.polocloud.database.exeuctes;

import de.polocloud.database.DatabaseExecutor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DatabaseAdd {

    public void addMoreInTable(String table, List<String> types, List<Object> list) {
        StringBuilder upload = new StringBuilder("INSERT INTO " + table + "(" + types.get(0));
        for (int i = 1; i < types.size(); i++) upload.append(", ").append(types.get(i));
        upload.append(") VALUES ('").append(list.get(0)).append("'");
        for (int i = 1; i < list.size(); i++) upload.append(", '").append(list.get(i)).append("'");
        upload.append(");");
        CompletableFuture.supplyAsync(() -> DatabaseExecutor.getDatabaseExecutor().executeUpdate(upload.toString()));
    }


}
