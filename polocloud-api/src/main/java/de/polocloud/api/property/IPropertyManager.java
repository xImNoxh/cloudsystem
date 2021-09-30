package de.polocloud.api.property;

import de.polocloud.api.common.PoloTypeUnsupportedActionException;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface IPropertyManager {

    /**
     * Loads all properties for a given player with a given {@link UUID}
     *
     * @param uniqueId the uuid of the player
     * @throws PoloTypeUnsupportedActionException if {@link de.polocloud.api.PoloCloudAPI} instance is plugin or wrapper
     */
    boolean loadProperties(UUID uniqueId) throws PoloTypeUnsupportedActionException;

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
     * @throws PoloTypeUnsupportedActionException if {@link de.polocloud.api.PoloCloudAPI} instance is plugin or wrapper
     */
    void saveAll() throws PoloTypeUnsupportedActionException;

    /**
     * Saves all cached {@link IProperty} of a given player
     * with a specific {@link UUID}
     *
     * @param uniqueId the uuid of the player
     * @throws PoloTypeUnsupportedActionException if {@link de.polocloud.api.PoloCloudAPI} instance is plugin or wrapper
     */
    void save(UUID uniqueId) throws PoloTypeUnsupportedActionException;

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
