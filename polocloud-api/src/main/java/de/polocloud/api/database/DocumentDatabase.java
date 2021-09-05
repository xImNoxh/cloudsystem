package de.polocloud.api.database;

import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.JsonData;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DocumentDatabase {

    /**
     * The unique id key for a document
     */
    public static final String NAME_KEY = "_db_id_key";

    /**
     * The name of this database
     */
    private final String name;

    /**
     * All cached documents
     */
    private final Map<String, JsonData> cache;

    /**
     * The directory of this database
     */
    private final File directory;

    public DocumentDatabase(String name) {
        this.name = name;
        this.cache = new HashMap<>();
        this.directory = new File(FileConstants.DATABASE_FOLDER, name + "/");
        this.loadCache();
    }

    /**
     * Loads the cache of this database
     */
    public void loadCache() {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File[] files = this.directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.getName().endsWith(".json")) {
                cache.put(file.getName().split(".json")[0], new JsonData(file));
            }
        }
    }

    /**
     * A list of all cached documents
     *
     * @return collection
     */
    public Collection<JsonData> getDocuments() {
        return this.cache.values();
    }

    /**
     * Gets a {@link JsonData} entry by key
     *
     * @param key the key
     * @return document
     */
    public JsonData getDocument(String key) {
        JsonData document = this.cache.get(key);

        if (document == null) {
            File file = new File(this.directory, key + ".json");
            if (file.exists()) {
                document = new JsonData(file);
                this.cache.put(file.getName().split(".json")[0], document);
            }
        }
        return document;
    }

    /**
     * Inserts {@link JsonData}s into database
     *
     * @param document the document
     */
    public void insert(JsonData document, String name) {
        document.append(NAME_KEY, name);
        if (document.has(NAME_KEY)) {
            this.cache.put(document.getString(NAME_KEY), document);
            document.save(new File(this.directory, document.getString(NAME_KEY) + ".json"));
        }
    }

    /**
     * Deletes an Entry
     *
     * @param name the name
     */
    public void delete(String name) {
        JsonData document = getDocument(name);
        if (document != null) {
            cache.remove(name);
        }
        document.delete();
    }

    /**
     * Deletes a {@link JsonData}
     *
     * @param document the document
     */
    public void delete(JsonData document) {
        if (document.has(NAME_KEY)) {
            delete(document.getString(NAME_KEY));
        }
    }

    /**
     * Saves the cached values
     */
    public void syncCache() {
        for (String s : this.cache.keySet()) {
            JsonData document = this.cache.get(s);
            document.save(new File(directory, s + ".json"));
        }
    }

    public String getName() {
        return name;
    }

    public Map<String, JsonData> getCache() {
        return cache;
    }

    public File getDirectory() {
        return directory;
    }
}
