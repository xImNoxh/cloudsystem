package de.polocloud.wrapper.network.handler;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.master.MasterLoginResponsePacket;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

public class MasterLoginResponsePacketHandler extends IPacketHandler<Packet> {
    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        MasterLoginResponsePacket packet = (MasterLoginResponsePacket) obj;

        Logger.log(LoggerType.INFO, packet.getMessage());

        if (!packet.isResponse()) {
            System.exit(-1);
        }

    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return MasterLoginResponsePacket.class;
    }
}
