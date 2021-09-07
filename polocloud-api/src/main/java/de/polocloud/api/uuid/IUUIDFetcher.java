package de.polocloud.api.uuid;

import java.util.UUID;

public interface IUUIDFetcher {


    /**
     * Loads a UUID by name
     * If its already cached it will be loaded from cache
     * otherwise it will be loaded from web-mojang-api
     *
     * @param playerName the name of the player
     * @return uniqueId
     */
    UUID getUniqueId(String playerName);

    /**
     * Gets the name of a player by its uuid
     *
     * @param uuid the uuid
     * @return name
     */
    String getName(UUID uuid);

    /**
     * Shuts down the executor
     */
    void shutdown();

    /**
     * Parses a UUID to String
     *
     * @param uuidAsString the string to be parsed
     * @return uuid
     */
    UUID parseUUIDFromString(String uuidAsString);
}
