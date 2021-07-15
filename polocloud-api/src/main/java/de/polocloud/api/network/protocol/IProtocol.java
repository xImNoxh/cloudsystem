package de.polocloud.api.network.protocol;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.channel.ChannelHandlerContext;

public interface IProtocol {

    void registerPacketHandler(IPacketHandler packetHandler);

    void firePacketHandlers(ChannelHandlerContext ctx, Packet packet);

}
