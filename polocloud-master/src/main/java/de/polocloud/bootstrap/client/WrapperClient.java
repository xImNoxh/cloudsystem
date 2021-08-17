package de.polocloud.bootstrap.client;

import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.gameserver.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.IPacketSender;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerStartPacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutionException;

public class WrapperClient implements IPacketSender {

    private String name;
    private ChannelHandlerContext chx;

    public WrapperClient(String name, ChannelHandlerContext ctx) {
        this.chx = ctx;
        this.name = name;
    }

    public void startServer(IGameServer gameServer) throws ExecutionException, InterruptedException {
        Logger.log(LoggerType.INFO, "Trying to start server " + ConsoleColors.LIGHT_BLUE + gameServer.getName() + ConsoleColors.GRAY + " on " + getName() + ".");

        ITemplate template = gameServer.getTemplate();

        int port = template.getTemplateType().equals(TemplateType.MINECRAFT) ? -1 : Master.getInstance().getPortService().getNextStartedPort();
        ((SimpleGameServer) gameServer).setPort(port);

        sendPacket(new MasterRequestServerStartPacket(port, template.getName(), template.getVersion(), gameServer.getSnowflake(),
            isProxy(template), template.getMaxMemory(), template.getMaxPlayers(), gameServer.getName(), gameServer.getMotd(), template.isStatic()));

        EventRegistry.fireEvent(new CloudGameServerStatusChangeEvent(gameServer, CloudGameServerStatusChangeEvent.Status.STARTING));
    }

    public String getName() {
        return name;
    }

    @Override
    public void sendPacket(Packet object) {
        this.chx.writeAndFlush(object);
    }

    public ChannelHandlerContext getConnection() {
        return this.chx;
    }

    public boolean isProxy(ITemplate template) {
        return template.getTemplateType() == TemplateType.PROXY;
    }

}
