package de.polocloud.api.messaging;

import de.polocloud.api.common.INamable;
import de.polocloud.api.common.ISnowflakeable;
import de.polocloud.api.network.protocol.packet.IPacketReceiver;

public interface IMessageChannel<T> extends INamable, ISnowflakeable {

    /**
     * Sends an object from this channel
     * to a given {@link IPacketReceiver} to handle the message
     *
     * @param t the object
     * @param receiver the receiver
     */
    void sendMessage(T t, IPacketReceiver... receiver);

    /**
     * Sends an object from this channel to all instances
     * that are able to receive Packets and Messages
     *
     * @param t the object
     */
    void sendMessage(T t);

    /**
     * Registers a {@link IMessageListener} for this channel
     * to handle incoming objects
     *
     * @param listener the listener
     */
    void registerListener(IMessageListener<T> listener);

    /**
     * Unregisters all listeners
     */
    void unregisterAll();
}
