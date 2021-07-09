package de.polocloud.bootstrap.client;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.IPacketSender;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerStartPacket;
import de.polocloud.api.template.TemplateType;
import io.netty.channel.ChannelHandlerContext;

public class WrapperClient implements IPacketSender {

    private ChannelHandlerContext chx;

    public WrapperClient(ChannelHandlerContext ctx) {
        this.chx = ctx;
    }

    public void startServer(IGameServer gameServer) {
        System.out.println("start server " + gameServer.getName());
        sendPacket(new MasterRequestServerStartPacket(
            gameServer.getTemplate().getName(),
            gameServer.getSnowflake(),
            gameServer.getTemplate().getTemplateType() == TemplateType.PROXY));
    }

    @Override
    public void sendPacket(IPacket object) {
        System.out.println("> " + object.getClass().getSimpleName());
        this.chx.writeAndFlush(object);
    }
}
