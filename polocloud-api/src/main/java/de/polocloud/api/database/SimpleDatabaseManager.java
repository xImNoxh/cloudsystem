package de.polocloud.api.database;

import de.polocloud.api.database.other.DatabaseType;
import de.polocloud.api.database.other.IDatabase;
import lombok.Getter;


@Getter
public class SimpleDatabaseManager implements IDatabaseManager {

    private IDatabase<?> currentDatabase;

    private String host;
    private int port;
    private String user;
    private String password;
    private String database;
    private String collectionOrTable;

    public SimpleDatabaseManager() {
        this.currentDatabase = null;
        this.setCredentials("127.0.0.1", 3306, "admin", "yourPw", "yourDb", "yourCollection");
    }

    @Override
    public void setDatabase(DatabaseType database) {
        this.currentDatabase = database.getDatabase();
        this.currentDatabase.setManager(this);
    }

    @Override
    public void setCredentials(String host, int port, String user, String password, String database, String collectionOrTable) {
        this.host = host;
        this.password = password;
        this.port = port;
        this.user = user;
        this.database = database;
        this.collectionOrTable = collectionOrTable;
    }

    @Override
    public void connect() throws Exception {
        this.currentDatabase.connect();
    }

    @Override
    public void close() throws Exception {
        this.currentDatabase.close();
    }
}
