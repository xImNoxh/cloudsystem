package de.polocloud.permission.api.database.adapter.executes.pull;

import de.polocloud.permission.api.database.adapter.DatabaseExecutor;

import java.sql.ResultSet;

public class DatabaseExist {

    public boolean existsInTable(String table, String key, Object value) {
        return DatabaseExecutor.getDatabaseExecutor().executeQuery(
            "SELECT * FROM " + table + " WHERE " + key + "='" + value + "'", ResultSet::next, false
        );
    }

    public boolean existsMoreInTable(String table, String key, String value, String k2, String v2) {
        return DatabaseExecutor.getDatabaseExecutor().executeQuery("SELECT * FROM " + table + " WHERE " + key + "='" + value + "' AND " +
            k2 + "='" + v2 + "'", ResultSet::next, false);
    }

}
