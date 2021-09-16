package de.polocloud.api.network.protocol.packet.handler;

import de.polocloud.api.network.protocol.packet.base.Packet;
import io.netty.channel.ChannelHandlerContext;

/**
 * This class is used to handle incoming {@link Packet}s
 *
 * @param <R> the generic-type (must extend Packet)
 */
public interface IPacketHandler<R extends Packet> {

    /**
     * Handles the received Packet-object from a given {@link ChannelHandlerContext}
     *
     * @param ctx the context
     * @param packet the packetObject
     */
    void handlePacket(ChannelHandlerContext ctx, R packet);

    /**
     * The Class of the {@link Packet} to listen for
     * to not handle every incoming packet but just packets
     * of the provided class
     */
    Class<? extends Packet> getPacketClass();

}
