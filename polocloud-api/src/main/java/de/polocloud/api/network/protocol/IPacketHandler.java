package de.polocloud.api.network.protocol;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.channel.ChannelHandlerContext;

public abstract class IPacketHandler {

    public abstract void handlePacket(ChannelHandlerContext ctx, Packet obj);

    public abstract Class<? extends Packet> getPacketClass();

}
