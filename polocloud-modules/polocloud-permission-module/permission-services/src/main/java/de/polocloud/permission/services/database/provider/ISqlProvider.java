package de.polocloud.permission.services.database.provider;

import de.polocloud.permission.api.database.adapter.DatabaseExecutor;
import de.polocloud.permission.services.Permissions;

public abstract class ISqlProvider {

    private final DatabaseExecutor sqlExecutor;
    private final String table;

    public ISqlProvider(String table) {
        this.sqlExecutor = Permissions.getInstance().getDatabaseService().executor();
        this.table = table;
        createTable();
    }

    public abstract void createTable();

    public String getTable() {
        return table;
    }

    public DatabaseExecutor getSqlExecutor() {
        return sqlExecutor;
    }


}
