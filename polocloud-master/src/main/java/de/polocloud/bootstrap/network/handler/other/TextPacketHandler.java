package de.polocloud.bootstrap.network.handler.other;

import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.packets.other.TextPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import io.netty.channel.ChannelHandlerContext;


public class TextPacketHandler implements IPacketHandler<TextPacket> {


    @Override
    public void handlePacket(ChannelHandlerContext ctx, TextPacket packet) {
        PoloLogger.print(LogLevel.INFO, packet.getText());
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return TextPacket.class;
    }
}
