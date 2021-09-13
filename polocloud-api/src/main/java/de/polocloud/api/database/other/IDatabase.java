package de.polocloud.api.database.other;

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
     * Connects this database
     */
    void connect() throws Exception;

    /**
     * Closes this database connection
     */
    void close() throws Exception;

}
