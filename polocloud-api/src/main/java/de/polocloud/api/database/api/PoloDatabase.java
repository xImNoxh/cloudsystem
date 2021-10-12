package de.polocloud.api.database.api;


import de.polocloud.api.config.JsonData;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class PoloDatabase<V> extends DocumentedDatabase {

    private final Class<V> vClass;

    public PoloDatabase(String name, Class<V> vClass) {
        super(name);
        this.vClass = vClass;
    }

    public PoloDatabase(String name, File directory, Class<V> vClass) {
        super(name, directory);
        this.vClass = vClass;
    }

    /**
     * Inserts an object into database
     *
     * @param key the key where its stored
     * @param object the database
     */
    public void insert(String key, V object) {
        JsonData document = new JsonData();
        document.append(object);
        this.insert(document, key);
    }

    public PoloDatabase<V> directory(File dir) {
        this.directory = dir;
        return this;
    }

    /**
     * Gets a list of all entries
     *
     * @return list of entries
     */
    public List<V> getEntries() {
        List<V> list = new LinkedList<>();

        for (JsonData document : this.getDocuments()) {
            list.add(document.getAs(vClass));
        }
        return list;
    }
}
