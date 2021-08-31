package de.polocloud.api.property;

import com.google.gson.JsonElement;

import java.util.function.Consumer;

public interface IProperty  {

    /**
     * The key of the property
     *
     * @return name as String
     */
    String getName();

    /**
     * Sets the name of this property
     *
     * @param key the name
     */
    void setName(String key);

    /**
     * Parses the object to a {@link JsonElement }and calls
     * {@link IProperty#setJsonValue(JsonElement)}
     *
     * @param value the value
     */
    void setValue(Object value);

    /**
     * The value of this property
     */
    JsonElement getJsonValue();

    /**
     * Checks if this property
     * has any other {@link IProperty}s
     * attached to it or just single
     */
    boolean isSingleProperty();

    /**
     * Attaches an {@link IProperty} to this property
     *
     * @param name the name
     * @param property the property
     */
    void addProperty(String name, Consumer<IProperty> property);

    /**
     * Copies all values of an {@link IProperty}
     * to this property
     *
     * @param property the copy
     */
    void copyFrom(IProperty property);

    /**
     * All attached {@link IProperty}s
     */
    IProperty[] getProperties();

    /**
     * Gets a sub {@link IProperty} by its name
     *
     * @param name the name
     * @return property or null if not found
     */
    IProperty getProperty(String name);

    /**
     * Gets a value by its type class
     *
     * @param typeClass the class
     * @param <T> the generic
     */
    <T> T getValue(Class<T> typeClass);

    /**
     * Sets the jsonObject of this property
     *
     * @param jsonObject the jsonObject
     */
    void setJsonValue(JsonElement jsonObject);

}
