package de.polocloud.api.wrapper;

import de.polocloud.api.gameserver.IGameServer;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface IWrapperManager {

    /**
     * Gets a list of all connected {@link IWrapper}s
     */
    List<IWrapper> getWrappers();

    /**
     * Gets an {@link IWrapper} by name
     *
     * @param name the name
     * @return wrapper or null if not found
     */
    IWrapper getWrapper(String name);

    /**
     * Gets an {@link IWrapper} by a channel handler context
     *
     * @param channelHandlerContext the ctx
     * @return wrapper or null if not found
     */
    IWrapper getWrapper(ChannelHandlerContext channelHandlerContext);


    /**
     * Registers an {@link IWrapper} in cache
     *
     * @param wrapper the wrapper
     */
    void registerWrapper(IWrapper wrapper);

    /**
     * Unregisters an {@link IWrapper} in cache
     *
     * @param wrapper the wrapper
     */
    void unregisterWrapper(IWrapper wrapper);

    /**
     * Updates the wrapper cache for all connections
     */
    void syncCache();

    /**
     * Updates the wrapper cache for a specific gameServer
     */
    void syncCache(IGameServer gameServer);
}
