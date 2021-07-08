package de.polocloud.bootstrap.client;

import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.IPacketSender;
import de.polocloud.api.network.protocol.packet.StartServerPacket;
import de.polocloud.api.template.ITemplate;
import io.netty.channel.ChannelHandlerContext;

public class WrapperClient implements IPacketSender {

    private ChannelHandlerContext chx;

    public WrapperClient(ChannelHandlerContext ctx) {
        this.chx = ctx;
    }

    public void startServer(ITemplate template){
        sendPacket(new StartServerPacket(template.getName()));
    }

    @Override
    public void sendPacket(IPacket object) {
        this.chx.writeAndFlush(object);
    }
}
