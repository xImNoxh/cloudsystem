package de.polocloud.bootstrap.network.handler.other;

import de.polocloud.api.network.packets.master.MasterShutdownPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.bootstrap.Master;
import io.netty.channel.ChannelHandlerContext;

public class ShutdownPacketHandler implements IPacketHandler<MasterShutdownPacket> {



    @Override
    public void handlePacket(ChannelHandlerContext ctx, MasterShutdownPacket packet) {
        Master.getInstance().terminate();
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return MasterShutdownPacket.class;
    }
}
