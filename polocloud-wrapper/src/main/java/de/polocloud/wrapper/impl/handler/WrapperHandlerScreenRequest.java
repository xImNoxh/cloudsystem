package de.polocloud.wrapper.impl.handler;

import de.polocloud.api.network.packets.wrapper.WrapperRequestLinesPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.wrapper.Wrapper;
import de.polocloud.wrapper.manager.screen.IScreen;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedList;

public class WrapperHandlerScreenRequest implements IPacketHandler<WrapperRequestLinesPacket> {

    @Override
    public void handlePacket(ChannelHandlerContext ctx, WrapperRequestLinesPacket packet) {
        String serverName = packet.getServerName();
        IScreen screen = Wrapper.getInstance().getScreenManager().getScreen(serverName);
        if (screen != null) {
            packet.respond("lines", screen.getCachedLines());
        } else {
            packet.respond("lines", new LinkedList<>());
        }
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return WrapperRequestLinesPacket.class;
    }
}
