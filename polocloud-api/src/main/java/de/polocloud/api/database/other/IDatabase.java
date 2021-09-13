package de.polocloud.api.database.other;

import de.polocloud.api.database.IDatabaseManager;

public interface IDatabase<V extends IDatabase<V>> {

    /**
     * The type of this {@link IDatabase}
     */
    DatabaseType getType();

    /**
     * Gets the Template loader to load templates
     * from this database
     */
    IDatabaseTemplateLoader<V> getTemplateLoader();

    /**
     * Gets the property loader to load properties
     * of different players
     */
    IDatabasePropertyLoader<V> getPropertyLoader();

    /**
     * The parent of this {@link IDatabase}
     */
    IDatabaseManager getManager();

    /**
     * Sets the manager of this database
     *
     * @param databaseManager the manager
     */
    void setManager(IDatabaseManager databaseManager);

    /**
     * Connects this database
     */
    void connect() throws Exception;

    /**
     * Closes this database connection
     */
    void close() throws Exception;

}
