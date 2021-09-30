package de.polocloud.api.network.protocol;

import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import io.netty.channel.ChannelHandlerContext;

public interface IProtocol {

    /**
     * Registers an {@link IPacketHandler} for a given Packet
     * in this {@link IProtocol}-Instance
     *
     * @param packetHandler the handler to register
     */
    void registerPacketHandler(IPacketHandler<? extends Packet> packetHandler);

    /**
     * Unregisters an {@link IPacketHandler}
     *
     * @param packetHandler the handler to unregister
     */
    void unregisterPacketHandler(IPacketHandler<?> packetHandler);

    /**
     * Handles all registered {@link IPacketHandler}s with a given
     * {@link ChannelHandlerContext} and the received {@link Packet} object
     *
     * @param ctx the channelHandlerContext
     * @param packet the packet object
     */
    void firePacketHandlers(ChannelHandlerContext ctx, Packet packet);

}
