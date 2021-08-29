package de.polocloud.api.fallback;

import de.polocloud.api.fallback.base.IFallback;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.player.ICloudPlayer;

import java.util.List;

public interface IFallbackManager {

    /**
     * Checks if a given {@link ICloudPlayer} is already on a fallback server
     *
     * @param cloudPlayer the player
     * @return boolean
     */
    boolean isOnFallback(ICloudPlayer cloudPlayer);

    /**
     * Gets a list of all available {@link IFallback}s
     */
    List<IFallback> getAvailableFallbacks();

    /**
     * Gets the highest accessible {@link IFallback} for a given {@link ICloudPlayer}
     *
     * @param cloudPlayer the player
     * @return the fallback or null if not found
     */
    IFallback getHighestFallback(ICloudPlayer cloudPlayer);

    /**
     * Gets a {@link IGameServer} for a {@link IFallback}
     *
     * @param fallback the fallback
     * @return gameserver
     */
    IGameServer getFallback(IFallback fallback);

    /**
     * Automatically gets a Fallback parsed as {@link IGameServer}
     * for a given {@link ICloudPlayer}
     *
     * @param cloudPlayer the player
     * @return server
     */
    IGameServer getFallback(ICloudPlayer cloudPlayer);

    /**
     * Registers an {@link IFallback} in cache
     *
     * @param fallback the fallback
     */
    void registerFallback(IFallback fallback);

    /**
     * Sets the available fallbacks
     *
     * @param fallbacks the fallbacks
     */
    void setAvailableFallbacks(List<IFallback> fallbacks);
}
