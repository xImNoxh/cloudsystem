package de.polocloud.bootstrap.listener;

import de.polocloud.api.event.ChannelActiveEvent;
import de.polocloud.api.event.EventHandler;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import io.netty.channel.ChannelHandlerContext;

public class ChannelActiveListener implements EventHandler<ChannelActiveEvent> {

    @Override
    public void handleEvent(ChannelActiveEvent event) {

    }
}
