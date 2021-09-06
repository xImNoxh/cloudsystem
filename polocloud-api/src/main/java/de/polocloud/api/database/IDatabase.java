package de.polocloud.api.database;

import de.polocloud.api.config.JsonData;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IDatabase<V> {


    /**
     * Loads the cache of this database
     */
    void loadCache();

    IDatabase<V> directory(File dir);

    /**
     * A list of all cached documents
     *
     * @return collection
     */
    Collection<JsonData> getDocuments();
    
    /**
     * Gets a {@link JsonData} entry by key
     *
     * @param key the key
     * @return document
     */
    JsonData getDocument(String key);

    /**
     * Deletes an Entry
     *
     * @param name the name
     */
    void delete(String name);

    /**
     * Saves the cached values
     */
    void syncCache();

    String getName();

    Map<String, JsonData> getCache();

    File getDirectory();
    
    /**
     * Inserts {@link JsonData}s into database
     *
     * @param document the document
     */
    void insert(JsonData document, String name);
    
    /**
     * Inserts an object into database
     *
     * @param key the key where its stored
     * @param object the database
     */
    void insert(String key, V object);

    /**
     * Gets a list of all entries
     *
     * @return list of entries
     */
    List<V> getEntries();
}
