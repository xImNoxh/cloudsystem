package de.polocloud.api.player;

import de.polocloud.api.pool.ObjectPool;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ICloudPlayerManager extends ObjectPool<ICloudPlayer> {

    @Override
    default void update(ICloudPlayer object) {
        if (this.getCached(object.getName()) == null) {
            this.register(object);
            return;
        }
        ObjectPool.super.update(object);
    }

    /**
     * Gets an {@link ICloudPlayer} by its uuid
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     *
     * @param uuid the uuid of the player
     */
    default ICloudPlayer getCached(UUID uuid) {
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
        return getCached(uuid) != null;
    }

}
