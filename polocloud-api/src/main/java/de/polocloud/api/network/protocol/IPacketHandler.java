package de.polocloud.api.network.protocol;

import de.polocloud.api.network.protocol.packet.IPacket;
import io.netty.channel.ChannelHandlerContext;

public abstract class IPacketHandler {

    public abstract void handlePacket(ChannelHandlerContext ctx, IPacket obj);

    public abstract Class<? extends IPacket> getPacketClass();

}
