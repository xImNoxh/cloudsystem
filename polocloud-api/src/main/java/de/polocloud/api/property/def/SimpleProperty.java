package de.polocloud.api.property.def;

import com.google.gson.JsonElement;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.property.IProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SimpleProperty implements IProperty {

    private String name;

    private JsonElement value;

    private final List<SimpleProperty> properties;

    public SimpleProperty() {
        this.name = "";
        this.properties = new ArrayList<>();
        this.value = null;
    }

    public SimpleProperty(String name, List<SimpleProperty> properties, JsonElement storage) {
        this.name = name;
        this.properties = properties;
        this.value = storage;
    }

    @Override
    public boolean isSingleProperty() {
        return this.properties.isEmpty();
    }

    @Override
    public void addProperty(String name, Consumer<IProperty> property) {
        SimpleProperty simpleProperty = new SimpleProperty();
        simpleProperty.setName(name);
        property.accept(simpleProperty);
        this.properties.add(simpleProperty);
    }

    @Override
    public void copyFrom(IProperty property) {
        this.setName(property.getName());
        this.setJsonValue(property.getJsonValue());

        this.properties.clear();
        for (IProperty propertyProperty : property.getProperties()) {
            this.properties.add((SimpleProperty) propertyProperty);
        }
    }

    @Override
    public IProperty getProperty(String name) {
        return this.properties.stream().filter(simpleProperty -> simpleProperty.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public IProperty[] getProperties() {
        return this.properties.toArray(new IProperty[0]);
    }

    @Override
    public void setName(String key) {
        this.name = key;
    }

    @Override
    public void setValue(Object value) {
        JsonElement jsonElement = JsonData.GSON.toJsonTree(value);
        this.setJsonValue(jsonElement);
    }

    @Override
    public <T> T getValue(Class<T> typeClass) {
        return JsonData.GSON.fromJson(this.value, typeClass);
    }

    @Override
    public void setJsonValue(JsonElement jsonObject) {
        this.value = jsonObject;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JsonElement getJsonValue() {
        return value;
    }


    @Override
    public String toString() {
        return "{\"" + name + "\" : " + value + "}";
    }
}
