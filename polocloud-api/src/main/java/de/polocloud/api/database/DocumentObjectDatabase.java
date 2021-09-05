package de.polocloud.api.database;


import de.polocloud.api.config.JsonData;

import java.util.LinkedList;
import java.util.List;

public class DocumentObjectDatabase<V> extends DocumentDatabase {

    private final Class<V> vClass;

    public DocumentObjectDatabase(String name, Class<V> vClass) {
        super(name);
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
        document.append(DocumentDatabase.NAME_KEY, key);
        document.append("object", object);

        this.insert(document, key);
    }

    /**
     * Gets a list of all entries
     *
     * @return list of entries
     */
    public List<V> getEntries() {
        List<V> list = new LinkedList<>();

        for (JsonData document : this.getDocuments()) {
            list.add(document.getObject("object", vClass));
        }
        return list;
    }
}
