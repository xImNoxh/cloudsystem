package de.polocloud.bootstrap.wrapper;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.packets.gameserver.GameServerUnregisterPacket;
import de.polocloud.api.network.packets.wrapper.WrapperRequestCPUUsage;
import de.polocloud.api.network.packets.wrapper.WrapperRequestUsedMemory;
import de.polocloud.api.network.packets.wrapper.WrapperUpdatePacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.master.MasterRequestServerStartPacket;
import de.polocloud.api.network.packets.master.MasterRequestsServerTerminatePacket;
import de.polocloud.api.network.packets.wrapper.WrapperRequestShutdownPacket;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import de.polocloud.api.network.protocol.packet.base.response.base.IResponse;
import de.polocloud.api.network.protocol.packet.base.response.def.Response;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.api.logger.PoloLogger;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class SimpleMasterWrapper implements IWrapper {

    private final String name;
    private ChannelHandlerContext chx;
    private final long memory;
    private final int maxSimultaneouslyStartingServices;
    private int currentlyStartingServices;
    private boolean authenticated;
    private final long snowflake;

    public SimpleMasterWrapper(String name, ChannelHandlerContext ctx, long memory, int maxSimultaneouslyStartingServices) {
        this.chx = ctx;
        this.name = name;
        this.memory = memory;
        this.maxSimultaneouslyStartingServices = maxSimultaneouslyStartingServices;
        this.snowflake = Snowflake.getInstance().nextId();
        this.authenticated = true;
        this.currentlyStartingServices = 0;
    }

    public void setChx(ChannelHandlerContext chx) {
        this.chx = chx;
    }

    @Override
    public void startServer(IGameServer gameServer){

        PoloCloudAPI.getInstance().getGameServerManager().register(gameServer);

        try {
            PoloLogger.print(LogLevel.INFO, "Requesting §3" + gameServer.getWrapper().getName() + " §7to start " + (gameServer.getTemplate().getTemplateType() == TemplateType.PROXY ? "proxy" : "server") + " §b" + gameServer.getName() + "§7#§b" + gameServer.getSnowflake() + " §7on port §e" + gameServer.getPort() + "§7!");

            gameServer.setStatus(GameServerStatus.STARTING);
            gameServer.updateInternally();

            sendPacket(new MasterRequestServerStartPacket(gameServer));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopServer(IGameServer gameServer) {

        for (ICloudPlayer cloudPlayer : gameServer.getPlayers()) {
            cloudPlayer.sendToFallbackExcept(gameServer.getName());
        }

        PoloCloudAPI.getInstance().sendPacket(new GameServerUnregisterPacket(gameServer.getSnowflake(), gameServer.getName()));
        sendPacket(new MasterRequestsServerTerminatePacket(gameServer));
    }

    public String getName() {
        return name;
    }

    @Override
    public long getUsedMemory() {
        WrapperRequestUsedMemory packet = new WrapperRequestUsedMemory(this.getName());
        IResponse response = PacketMessenger
            .create()
            .blocking()
            .timeOutAfter(TimeUnit.SECONDS, 1)
            .orElse(new Response(new JsonData("memory", "100")))
            .setUpPassOn()
            .target(PoloType.WRAPPER)
            .send(packet);

        return response.get("memory").getAsInt();
    }



    @Override
    public float getCpuUsage() {
        WrapperRequestCPUUsage packet = new WrapperRequestCPUUsage(this.getName());
        IResponse response = PacketMessenger
            .create()
            .blocking()
            .timeOutAfter(TimeUnit.SECONDS, 1)
            .orElse(new Response(new JsonData("cpu", "100")))
            .setUpPassOn()
            .target(PoloType.WRAPPER)
            .send(packet);

        return response.get("cpu").getAsFloat();
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
    public long getMaxMemory() {
        return memory;
    }

    @Override
    public ChannelHandlerContext ctx() {
        return chx;
    }

    @Override
    public void update() {
        PoloCloudAPI.getInstance().sendPacket(new WrapperUpdatePacket(this));
    }

    @Override
    public void sendPacket(Packet packet) {
        if (!isStillConnected()) {
            return;
        }
        this.chx.writeAndFlush(packet).addListener((ChannelFutureListener) channelFuture -> {
            if (!channelFuture.isSuccess()) {
                if (channelFuture.cause() instanceof IOException) {
                     return;
                }
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
        try {
            int count = getServers().size();
            for (IGameServer gameServer : getServers()) {
                gameServer.terminate();
                count--;
                if (count <= 0) {
                    sendPacket(new WrapperRequestShutdownPacket());
                    return true;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
