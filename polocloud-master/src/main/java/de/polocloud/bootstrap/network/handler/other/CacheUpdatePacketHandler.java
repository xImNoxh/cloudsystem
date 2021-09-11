package de.polocloud.bootstrap.network.handler.other;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.packets.api.CacheRequestPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import io.netty.channel.ChannelHandlerContext;


public class CacheUpdatePacketHandler implements IPacketHandler<CacheRequestPacket> {


    @Override
    public void handlePacket(ChannelHandlerContext ctx, CacheRequestPacket packet) {
        PoloCloudAPI.getInstance().reload();
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return CacheRequestPacket.class;
    }
}
