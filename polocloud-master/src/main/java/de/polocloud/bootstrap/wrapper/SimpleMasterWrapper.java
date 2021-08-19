package de.polocloud.bootstrap.wrapper;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.gameserver.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerStartPacket;
import de.polocloud.api.network.protocol.packet.master.MasterRequestsServerTerminatePacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRequestShutdownPacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.TemplateType;
import de.polocloud.api.util.PoloUtils;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.wrapper.IWrapper;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SimpleMasterWrapper extends PoloUtils implements IWrapper {

    private final String name;
    private final ChannelHandlerContext chx;
    private final long snowflake;

    public SimpleMasterWrapper(String name, ChannelHandlerContext ctx) {
        this.chx = ctx;
        this.name = name;
        this.snowflake = Snowflake.getInstance().nextId();
    }

    @Override
    public void startServer(IGameServer gameServer){
        PoloCloudAPI.getInstance().getGameServerManager().registerGameServer(gameServer);
        try {
            Logger.log(LoggerType.INFO, "Trying to start server " + ConsoleColors.LIGHT_BLUE + gameServer.getName() + ConsoleColors.GRAY + " on " + getName() + ".");

            ITemplate template = gameServer.getTemplate();

            int port = template.getTemplateType().equals(TemplateType.MINECRAFT) ? -1 : Master.getInstance().getPortService().getNextStartedPort();
            gameServer.setPort(port);

            sendPacket(new MasterRequestServerStartPacket(port, template.getName(), template.getVersion(), gameServer.getSnowflake(),
                isProxy(template), template.getMaxMemory(), template.getMaxPlayers(), gameServer.getName(), gameServer.getMotd(), template.isStatic()));

            EventRegistry.fireEvent(new CloudGameServerStatusChangeEvent(gameServer, CloudGameServerStatusChangeEvent.Status.STARTING));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopServer(IGameServer gameServer) {
        sendPacket(new MasterRequestsServerTerminatePacket(gameServer));
    }

    public String getName() {
        return name;
    }

    @Override
    public List<IGameServer> getServers() {
        List<IGameServer> list = new LinkedList<>();
        List<IGameServer> gameServers = sneakyThrows(() -> PoloCloudAPI.getInstance().getGameServerManager().getGameServers().get());
        for (IGameServer gameServer : gameServers) {
            if (gameServer.getWrapper().getName().equalsIgnoreCase(this.name)) {
                list.add(gameServer);
            }
        }
        return list;
    }

    @Override
    public long getSnowflake() {
        return snowflake;
    }

    @Override
    public ChannelHandlerContext ctx() {
        return chx;
    }

    @Override
    public void sendPacket(Packet packet) {
        this.chx.writeAndFlush(packet);
    }

    public ChannelHandlerContext getConnection() {
        return this.chx;
    }

    public boolean isProxy(ITemplate template) {
        return template.getTemplateType() == TemplateType.PROXY;
    }

    @Override
    public boolean terminate() {
        int count = getServers().size();
        for (IGameServer gameServer : getServers()) {
            gameServer.terminate();
            count--;
            if (count <= 0) {
                sendPacket(new WrapperRequestShutdownPacket());
                return true;
            }
        }
        return false;
    }
}
