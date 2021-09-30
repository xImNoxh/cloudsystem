package de.polocloud.api.property;

import java.util.List;
import java.util.function.Consumer;

public interface IPropertyHolder {

    /**
     * Gets a list of all {@link IProperty} this holder has
     *
     * @return list of properties
     */
    List<IProperty> getProperties();

    /**
     * Gets an {@link IProperty} by its name
     *
     * @param name the name
     * @return property or null if not found
     */
    IProperty getProperty(String name);

    /**
     * Checks if a property is exiting
     *
     * @param name the name of the property
     * @return boolean
     */
    boolean hasProperty(String name);

    /**
     * Adds or overrides an {@link IProperty}
     *
     * @param consumer the consumer
     */
    void insertProperty(Consumer<IProperty> consumer);

    /**
     * Deletes an {@link IProperty} of this holder
     *
     * @param name the name of the property
     */
    void deleteProperty(String name);

}
