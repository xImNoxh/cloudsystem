package de.polocloud.bootstrap.network.handler.other;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.SimpleCachedEventManager;
import de.polocloud.api.network.packets.api.EventPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import io.netty.channel.ChannelHandlerContext;

public class EventPacketHandler implements IPacketHandler<EventPacket> {

    @Override
    public void handlePacket(ChannelHandlerContext ctx, EventPacket packet) {

        if (!packet.getExcept().equalsIgnoreCase("null") && packet.getExcept().equalsIgnoreCase("cloud")) {
            return;
        }

        PoloCloudAPI.getInstance().getEventManager().fireEvent(packet.getEvent());
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return EventPacket.class;
    }
}
