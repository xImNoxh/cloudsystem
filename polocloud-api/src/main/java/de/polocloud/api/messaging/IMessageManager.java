package de.polocloud.api.messaging;

import java.util.List;

public interface IMessageManager {

    /**
     * Registers a {@link IMessageChannel} for a certain generic-Type and a channel name
     *
     * @param wrapperClass the class of the generic
     * @param channelName the name of the channel
     * @param <T> the generic
     * @return created message channel
     */
    <T> IMessageChannel<T> registerChannel(Class<T> wrapperClass, String channelName);

    /**
     * Gets a {@link IMessageChannel} by its name
     *
     * @param channelName the name
     * @param <T> the type
     * @return channel or null if not found
     */
    <T> IMessageChannel<T> getChannel(String channelName);

    /**
     * Gets a list of all registered {@link IMessageChannel}s
     */
    List<IMessageChannel<?>> getChannels();
}
