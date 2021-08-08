package de.polocloud.permission.api.database.adapter;

import de.polocloud.permission.api.database.DatabaseSQL;
import de.polocloud.permission.api.database.adapter.executes.DatabaseAdd;
import de.polocloud.permission.api.database.adapter.executes.DatabaseRemove;
import de.polocloud.permission.api.database.adapter.executes.DatabaseUpdate;
import de.polocloud.permission.api.database.adapter.executes.pull.DatabaseExist;
import de.polocloud.permission.api.database.adapter.executes.pull.DatabaseGet;
import de.polocloud.permission.api.database.adapter.executes.table.DatabaseTable;

public class DatabaseService {

    private DatabaseSQL databaseSQL;

    private final DatabaseExist databaseExist;
    private final DatabaseGet databaseGet;
    private final DatabaseTable databaseTable;
    private final DatabaseAdd databaseAdd;
    private final DatabaseRemove databaseRemove;
    private final DatabaseUpdate databaseUpdate;

    private final DatabaseExecutor databaseExecutor;

    public DatabaseService() {

        this.databaseSQL = new DatabaseSQL();

        this.databaseExist = new DatabaseExist();
        this.databaseGet = new DatabaseGet();
        this.databaseTable = new DatabaseTable();
        this.databaseAdd = new DatabaseAdd();
        this.databaseRemove = new DatabaseRemove();
        this.databaseUpdate = new DatabaseUpdate();

        this.databaseExecutor = new DatabaseExecutor(databaseSQL);

    }


    public DatabaseExist exist() {
        return this.databaseExist;
    }

    public DatabaseGet get() {
        return this.databaseGet;
    }

    public DatabaseTable table() {
        return this.databaseTable;
    }

    public DatabaseSQL getDatabaseSQL() {
        return databaseSQL;
    }

    public DatabaseAdd add() {
        return this.databaseAdd;
    }

    public DatabaseRemove remove() {
        return this.databaseRemove;
    }

    public DatabaseUpdate update() {
        return this.databaseUpdate;
    }

    public DatabaseExecutor executor() {
        return this.databaseExecutor;
    }
}
