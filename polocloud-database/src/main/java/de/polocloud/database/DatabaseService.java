package de.polocloud.database;

import de.polocloud.database.exeuctes.DatabaseAdd;
import de.polocloud.database.exeuctes.DatabaseRemove;
import de.polocloud.database.exeuctes.DatabaseUpdate;
import de.polocloud.database.exeuctes.pull.DatabaseExist;
import de.polocloud.database.exeuctes.pull.DatabaseGet;
import de.polocloud.database.exeuctes.table.DatabaseTable;
public class DatabaseService {

    private final DatabaseExist databaseExist;
    private final DatabaseGet databaseGet;
    private final DatabaseTable databaseTable;
    private final DatabaseAdd databaseAdd;
    private final DatabaseRemove databaseRemove;
    private final DatabaseUpdate databaseUpdate;

    private final DatabaseConnector databaseConnector;
    private final DatabaseExecutor databaseExecutor;

    public DatabaseService() {
        this.databaseExist = new DatabaseExist();
        this.databaseGet = new DatabaseGet();
        this.databaseTable = new DatabaseTable();
        this.databaseAdd = new DatabaseAdd();
        this.databaseRemove = new DatabaseRemove();
        this.databaseUpdate = new DatabaseUpdate();
        this.databaseConnector = new DatabaseConnector("localhost", "admin", "NMr0k47K2tCLp8MlGMDC", "claymc", 3306);
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
