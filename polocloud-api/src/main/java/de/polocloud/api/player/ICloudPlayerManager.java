package de.polocloud.api.player;

import de.polocloud.api.template.ITemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ICloudPlayerManager {

    /**
     * Registers an {@link ICloudPlayer} in cache
     *
     * @param cloudPlayer the cloudPlayer
     */
    void register(ICloudPlayer cloudPlayer);

    /**
     * Unregisters an {@link ICloudPlayer} in cache
     *
     * @param cloudPlayer the cloudPlayer
     */
    void unregister(ICloudPlayer cloudPlayer);

    /**
     * Gets a collection of all loaded {@link ICloudPlayer}s
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     */
    CompletableFuture<List<ICloudPlayer>> getAllOnlinePlayers();

    /**
     * Gets an {@link ICloudPlayer} by its name
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     *
     * @param name the name of the player
     */
    CompletableFuture<ICloudPlayer> getOnlinePlayer(String name);

    /**
     * Gets an {@link ICloudPlayer} by its uuid
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     *
     * @param uuid the uuid of the player
     */
    CompletableFuture<ICloudPlayer> getOnlinePlayer(UUID uuid);

    /**
     * Checks if an {@link ICloudPlayer} is online
     *
     * @param name the name of the player
     */
    CompletableFuture<Boolean> isPlayerOnline(String name);

    /**
     * Checks if an {@link ICloudPlayer} is online
     *
     * @param uuid the uuid of the player
     */
    CompletableFuture<Boolean> isPlayerOnline(UUID uuid);

}
