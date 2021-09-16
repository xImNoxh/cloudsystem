package de.polocloud.api.database;

import de.polocloud.api.database.other.DatabaseType;
import de.polocloud.api.database.other.IDatabase;

public interface IDatabaseManager {

    /**
     * The current selected {@link IDatabase}
     */
    IDatabase<?> getCurrentDatabase();

    /**
     * Sets the current {@link IDatabase}
     *
     * @param database the type of database
     */
    void setDatabase(DatabaseType database);

    /**
     * Sets the credentials of the current {@link IDatabase}
     *
     * @param host the host to connect to
     * @param port the port to connect to
     * @param user the username to connect with
     * @param password the password to connect with
     * @param database the default database
     * @param collectionOrTable the collection (if MongoDB) or the table (if MySQL)
     */
    void setCredentials(String host, int port, String user, String password, String database, String collectionOrTable);

    /**
     * The provided host
     */
    String getHost();

    /**
     * The provided port
     */
    int getPort();

    /**
     * The provided user
     */
    String getUser();

    /**
     * The provided password
     */
    String getPassword();

    /**
     * The provided database
     */
    String getDatabase();

    /**
     * The provided collectionOrTable
     */
    String getCollectionOrTable();

    /**
     * Connects the database
     */
    void connect() throws Exception;

    /**
     * Closes the database
     */
    void close() throws Exception;
}
