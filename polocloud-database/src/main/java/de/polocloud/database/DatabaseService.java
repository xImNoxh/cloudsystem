package de.polocloud.database;

import de.polocloud.database.executes.DatabaseAdd;
import de.polocloud.database.executes.DatabaseRemove;
import de.polocloud.database.executes.DatabaseUpdate;
import de.polocloud.database.executes.pull.DatabaseExist;
import de.polocloud.database.executes.pull.DatabaseGet;
import de.polocloud.database.executes.table.DatabaseTable;

public class DatabaseService {

    private final DatabaseExist databaseExist;
    private final DatabaseGet databaseGet;
    private final DatabaseTable databaseTable;
    private final DatabaseAdd databaseAdd;
    private final DatabaseRemove databaseRemove;
    private final DatabaseUpdate databaseUpdate;

    private final DatabaseConnector databaseConnector;
    private final DatabaseExecutor databaseExecutor;

    public DatabaseService(String hostname, String username, String password, String database, int port) {
        this.databaseExist = new DatabaseExist();
        this.databaseGet = new DatabaseGet();
        this.databaseTable = new DatabaseTable();
        this.databaseAdd = new DatabaseAdd();
        this.databaseRemove = new DatabaseRemove();
        this.databaseUpdate = new DatabaseUpdate();
        this.databaseConnector = new DatabaseConnector(hostname, username, password, database, port);
        this.databaseExecutor = new DatabaseExecutor(this.databaseConnector);
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

    public DatabaseAdd add() {
        return this.databaseAdd;
    }

    public DatabaseRemove remove() {
        return this.databaseRemove;
    }

    public DatabaseUpdate update() {
        return this.databaseUpdate;
    }

    public DatabaseConnector connector() {
        return this.databaseConnector;
    }

    public DatabaseExecutor executor() {
        return this.databaseExecutor;
    }


}
