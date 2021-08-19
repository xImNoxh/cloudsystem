package de.polocloud.api.network.protocol;

import de.polocloud.api.network.protocol.packet.Packet;
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
     * @param obj the packetObject
     */
    void handlePacket(ChannelHandlerContext ctx, R obj);

    /**
     * The Class of the {@link Packet} to listen for
     * to not handle every incoming packet but just packets
     * of the provided class
     */
    Class<? extends Packet> getPacketClass();

}
