package de.polocloud.api.player;

import de.polocloud.api.network.protocol.packet.base.response.extra.INetworkPromise;
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
     * Retrieves an {@link INetworkPromise} containing the requested
     * {@link ICloudPlayer} searched for by its name
     *
     * @param name the name of the player
     * @return element to handle
     */
    INetworkPromise<ICloudPlayer> get(String name);

    /**
     * Retrieves an {@link INetworkPromise} containing the requested
     * {@link ICloudPlayer} searched for by its uuid
     *
     * @param uniqueId the uuid of the player
     * @return element to handle
     */
    INetworkPromise<ICloudPlayer> get(UUID uniqueId);

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

}
