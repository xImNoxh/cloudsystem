package de.polocloud.api.wrapper;

import de.polocloud.api.wrapper.base.IWrapper;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public interface IWrapperManager {

    /**
     * Gets a list of all connected {@link IWrapper}s
     */
    List<IWrapper> getWrappers();

    /**
     * Sets the cached {@link IWrapper} obejcts
     *
     * @param wrappers the cache
     */
    void setCachedObjects(List<IWrapper> wrappers);

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

}
