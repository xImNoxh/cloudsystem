package de.polocloud.bootstrap.wrapper;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.packets.gameserver.GameServerUnregisterPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.master.MasterRequestServerStartPacket;
import de.polocloud.api.network.packets.master.MasterRequestsServerTerminatePacket;
import de.polocloud.api.network.packets.wrapper.WrapperRequestShutdownPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.api.logger.PoloLogger;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedList;
import java.util.List;

public class SimpleMasterWrapper extends PoloHelper implements IWrapper {

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
            PoloLogger.print(LogLevel.INFO, "Requesting §3" + gameServer.getWrapper().getName() + " §7to start " + (gameServer.getTemplate().getTemplateType() == TemplateType.PROXY ? "proxy" : "server") + " §b" + gameServer.getName() + "§7#§b" + gameServer.getSnowflake() + " §7on port §e" + gameServer.getPort() + "§7!");

            gameServer.setStatus(GameServerStatus.STARTING);
            gameServer.update();

            sendPacket(new MasterRequestServerStartPacket(gameServer.getName(), gameServer.getPort()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopServer(IGameServer gameServer) {
        PoloCloudAPI.getInstance().sendPacket(new GameServerUnregisterPacket(gameServer.getSnowflake(), gameServer.getName()));
        PoloCloudAPI.getInstance().getGameServerManager().unregisterGameServer(PoloCloudAPI.getInstance().getGameServerManager().getCached(gameServer.getName()));
        PoloCloudAPI.getInstance().getPortManager().removePort(gameServer.getPort());
        for (ICloudPlayer cloudPlayer : gameServer.getCloudPlayers()) {
            cloudPlayer.sendToFallbackExcept(gameServer.getName());
        }

        sendPacket(new MasterRequestsServerTerminatePacket(gameServer));
    }

    public String getName() {
        return name;
    }

    @Override
    public List<IGameServer> getServers() {
        List<IGameServer> list = new LinkedList<>();
        List<IGameServer> gameServers = PoloCloudAPI.getInstance().getGameServerManager().getAllCached();
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
        if (!isStillConnected()) {
            return;
        }
        this.chx.writeAndFlush(packet).addListener((ChannelFutureListener) channelFuture -> {
            if (!channelFuture.isSuccess()) {
                System.out.println("[SimpleMasterWrapper@" + packet.getClass().getSimpleName() + "] Ran into error while processing Packet :");
                channelFuture.cause().printStackTrace();
            }
        });
    }

    @Override
    public boolean isStillConnected() {
        return !this.chx.isRemoved() && this.chx.channel().isRegistered() && this.chx.channel().isActive() && this.chx.channel().isOpen() && this.chx.channel().isWritable();
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
