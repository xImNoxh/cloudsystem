package de.polocloud.api.wrapper.base;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.packets.gameserver.GameServerUnregisterPacket;
import de.polocloud.api.network.packets.master.MasterRequestServerStartPacket;
import de.polocloud.api.network.packets.master.MasterStopServerPacket;
import de.polocloud.api.network.packets.wrapper.WrapperRequestCPUUsage;
import de.polocloud.api.network.packets.wrapper.WrapperRequestUsedMemory;
import de.polocloud.api.network.packets.wrapper.WrapperUpdatePacket;
import de.polocloud.api.network.protocol.packet.base.other.ForwardingPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.master.MasterRequestsServerTerminatePacket;
import de.polocloud.api.network.packets.master.MasterStartServerPacket;
import de.polocloud.api.network.packets.wrapper.WrapperRequestShutdownPacket;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import de.polocloud.api.network.protocol.packet.base.response.base.IResponse;
import de.polocloud.api.network.protocol.packet.base.response.def.Response;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.helper.TemplateType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class SimpleWrapper implements IWrapper {

    private final String name;
    private final long snowflake;
    private final long memory;
    private final int maxSimultaneouslyStartingServices;
    private int currentlyStartingServices;
    private boolean authenticated;
    private ChannelHandlerContext ctx;


    public SimpleWrapper(String name, long snowflake, long memory, int maxSimultaneouslyStartingServices, int currentlyStartingServices, boolean authenticated, ChannelHandlerContext ctx) {
        this.name = name;
        this.memory = memory;
        this.snowflake = snowflake;
        this.maxSimultaneouslyStartingServices = maxSimultaneouslyStartingServices;
        this.currentlyStartingServices = currentlyStartingServices;
        this.authenticated = authenticated;
        this.ctx = ctx;
    }


    @Override
    public boolean isStillConnected() {
        if (this.ctx == null) {
            return false;
        }
        return !this.ctx.isRemoved() && this.ctx.channel().isRegistered() && this.ctx.channel().isActive() && this.ctx.channel().isOpen() && this.ctx.channel().isWritable();
    }
    
    @Override
    public boolean terminate() {
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
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
        } else {
            sendPacket(new WrapperRequestShutdownPacket());
        }
        return true;
    }

    @Override
    public void sendPacket(Packet packet) {

        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER && ctx != null) {
            if (!isStillConnected()) {
                System.out.println("[SimpleWrapper@" + packet.getClass().getSimpleName() + "] Tried sending packet while not being connected (anymore) !");
                return;
            }

            this.ctx.writeAndFlush(packet).addListener((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    if (channelFuture.cause() instanceof IOException) {
                        return;
                    }
                    System.out.println("[SimpleWrapper@" + packet.getClass().getSimpleName() + "] Ran into error while processing Packet :");
                    channelFuture.cause().printStackTrace();
                }
            });
        } else {
            Packet forwardingPacket = new ForwardingPacket(PoloType.WRAPPER, this.name, packet);
            PoloCloudAPI.getInstance().getConnection().sendPacket(forwardingPacket);
        }
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
    public String getName() {
        return this.name;
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
    public long getMaxMemory() {
        return memory;
    }

    @Override
    public ChannelHandlerContext ctx() {
        return ctx;
    }

    @Override
    public void update() {
        WrapperUpdatePacket wrapperUpdatePacket = new WrapperUpdatePacket(this);
        PoloCloudAPI.getInstance().sendPacket(wrapperUpdatePacket);
    }

    @Override
    public void startServer(IGameServer gameServer) {
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {

            PoloCloudAPI.getInstance().getGameServerManager().register(gameServer);

            try {
                PoloLogger.print(LogLevel.INFO, "Requesting §3" + gameServer.getWrapper().getName() + " §7to start " + (gameServer.getTemplate().getTemplateType() == TemplateType.PROXY ? "proxy" : "server") + " §b" + gameServer.getName() + "§7#§b" + gameServer.getSnowflake() + " §7on port §e" + gameServer.getPort() + "§7!");

                gameServer.setStatus(GameServerStatus.STARTING);
                gameServer.updateInternally();

                sendPacket(new MasterRequestServerStartPacket(gameServer));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (PoloCloudAPI.getInstance().getGameServerManager().getCached(gameServer.getName()) != null) {
            return;
        }
        PoloCloudAPI.getInstance().getGameServerManager().register(gameServer);

        if (PoloCloudAPI.getInstance().getType().isPlugin()) {
            PoloCloudAPI.getInstance().sendPacket(new MasterStartServerPacket(gameServer, this));
        } else {
            PoloCloudAPI.getInstance().sendPacket(new MasterRequestServerStartPacket(gameServer));
        }
    }

    @Override
    public void stopServer(IGameServer gameServer) {
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {

            for (ICloudPlayer cloudPlayer : gameServer.getPlayers()) {
                cloudPlayer.sendToFallbackExcept(gameServer.getName());
            }

            PoloCloudAPI.getInstance().sendPacket(new GameServerUnregisterPacket(gameServer.getSnowflake(), gameServer.getName()));
            sendPacket(new MasterRequestsServerTerminatePacket(gameServer));
            return;
        }

        for (ICloudPlayer cloudPlayer : gameServer.getPlayers()) {
            cloudPlayer.sendToFallbackExcept(gameServer.getName());
        }

        PoloCloudAPI.getInstance().sendPacket(new GameServerUnregisterPacket(gameServer.getSnowflake(), gameServer.getName()));

        if (PoloCloudAPI.getInstance().getType().isPlugin()) {
            PoloCloudAPI.getInstance().sendPacket(new MasterStopServerPacket(gameServer, this));
        } else {
            sendPacket(new MasterRequestsServerTerminatePacket(gameServer));
        }
    }
}
