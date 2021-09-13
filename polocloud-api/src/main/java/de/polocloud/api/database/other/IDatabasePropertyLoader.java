package de.polocloud.api.database.other;

import de.polocloud.api.property.IProperty;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface IDatabasePropertyLoader<V extends IDatabase<V>> {

    /**
     * Loads all {@link IProperty} and puts them into a {@link List}
     * and puts this list into a {@link Map} with the {@link UUID} of the property owner
     */
    Map<UUID, List<IProperty>> loadProperties();


    /**
     * Gets all {@link IProperty} for a player with a given {@link UUID}
     *
     * @param uniqueId the uuid of the player
     * @return list or empty list if not found
     */
    List<IProperty> getProperties(UUID uniqueId);

    /**
     * Saves all cached {@link IProperty}s
     * But only if this instance is Master
     *
     */
    void saveAll();

    /**
     * Saves all cached {@link IProperty} of a given player
     * with a specific {@link UUID}
     *
     * @param uniqueId the uuid of the player
     */
    void save(UUID uniqueId);

    /**
     * Gets an {@link IProperty} for specific player with a given {@link UUID}
     *
     * @param uniqueId the uuid of the player
     * @param name the name of the property
     * @return property or null if not found
     */
    IProperty getProperty(UUID uniqueId, String name);

    /**
     * Inserts an {@link IProperty} into MasterCache
     * If it doesn't exist it will create one
     * Otherwise it will override it
     *
     * @param uuid the uuid
     * @param property the property
     */
    void insertProperty(UUID uuid, Consumer<IProperty> property);

    /**
     * Deletes an {@link IProperty} from MasterCache
     *
     * @param uuid the uuid
     * @param property the name of the property
     */
    void deleteProperty(UUID uuid, String property);
}
