package de.polocloud.api.util.other;

import de.polocloud.api.config.JsonData;

public class Task {

    private final String name;
    private final String document;

    public Task(String name, JsonData document) {
        this.name = name;
        this.document = document.toString();
    }

    public String getName() {
        return name;
    }

    public JsonData getDocument() {
        return new JsonData(document);
    }
}
