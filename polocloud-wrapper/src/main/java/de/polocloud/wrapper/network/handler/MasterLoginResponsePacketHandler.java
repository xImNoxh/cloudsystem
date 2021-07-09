package de.polocloud.wrapper.network.handler;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.master.MasterLoginResponsePacket;
import io.netty.channel.ChannelHandlerContext;

public class MasterLoginResponsePacketHandler extends IPacketHandler {
    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
        MasterLoginResponsePacket packet = (MasterLoginResponsePacket) obj;

        System.out.println(packet.getMessage());

        if(!packet.isResponse()){
            System.exit(-1);
        }

    }

    @Override
    public Class<? extends IPacket> getPacketClass() {
        return MasterLoginResponsePacket.class;
    }
}
