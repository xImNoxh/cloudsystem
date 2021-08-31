package de.polocloud.database;

import de.polocloud.database.types.DatabaseType;

public interface IDatabaseConnector {

    void connect(DatabaseType databaseType);

    void close();

}
