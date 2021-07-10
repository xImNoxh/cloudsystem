package de.polocloud.bootstrap.listener;

import de.polocloud.api.network.event.ChannelActiveEvent;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

public class ChannelActiveListener extends IPacketHandler {
    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {

    }

    @Override
    public Class<? extends IPacket> getPacketClass() {
        return ChannelActiveEvent.class;
    }
}
