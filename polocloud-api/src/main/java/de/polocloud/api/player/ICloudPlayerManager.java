package de.polocloud.api.player;

import de.polocloud.api.pool.ObjectPool;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ICloudPlayerManager extends ObjectPool<ICloudPlayer> {

    /**
     * Registers an {@link ICloudPlayer} in cache
     *
     * @param cloudPlayer the cloudPlayer
     */
    void registerPlayer(ICloudPlayer cloudPlayer);

    @Override
    default void updateObject(ICloudPlayer object) {
        if (this.getCached(object.getName()) == null) {
            this.registerPlayer(object);
            return;
        }
        ObjectPool.super.updateObject(object);
    }

    /**
     * Unregisters an {@link ICloudPlayer} in cache
     *
     * @param cloudPlayer the cloudPlayer
     */
    void unregisterPlayer(ICloudPlayer cloudPlayer);

    /**
     * Gets an {@link ICloudPlayer} by its uuid
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     *
     * @param uuid the uuid of the player
     */
    default ICloudPlayer getCachedObject(UUID uuid) {
        return getOptional(uuid).orElse(null);
    }

    /**
     * Loads an {@link Optional} for the object
     *
     * @param uniqueId the uuid of the player
     * @return optional
     */
    default Optional<ICloudPlayer> getOptional(UUID uniqueId) {
        return getAllCached().stream().filter(cloudPlayer -> cloudPlayer.getUUID().equals(uniqueId)).findFirst();
    }

    /**
     * Checks if an {@link ICloudPlayer} is online
     *
     * @param name the name of the player
     */
    default boolean isPlayerOnline(String name) {
        return getCached(name) != null;
    }

    /**
     * Checks if an {@link ICloudPlayer} is online
     *
     * @param uuid the uuid of the player
     */
    default boolean isPlayerOnline(UUID uuid) {
        return getCachedObject(uuid) != null;
    }

}
