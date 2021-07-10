package de.polocloud.bootstrap.client;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.IPacketSender;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerStartPacket;
import de.polocloud.api.template.TemplateType;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

public class WrapperClient implements IPacketSender {

    private String name;
    private ChannelHandlerContext chx;

    public WrapperClient(String name, ChannelHandlerContext ctx) {
        this.chx = ctx;
        this.name = name;
    }

    public void startServer(IGameServer gameServer) {
        Logger.log(LoggerType.INFO, "Start server " + gameServer.getName() + " on Wrapper " +  getName());
        sendPacket(new MasterRequestServerStartPacket(
            gameServer.getTemplate().getName(),
            gameServer.getTemplate().getVersion(), gameServer.getSnowflake(),
            gameServer.getTemplate().getTemplateType() == TemplateType.PROXY));
    }

    public String getName() {
        return name;
    }

    @Override
    public void sendPacket(IPacket object) {
        this.chx.writeAndFlush(object);
    }

    public ChannelHandlerContext getConnection(){
        return this.chx;
    }

}
