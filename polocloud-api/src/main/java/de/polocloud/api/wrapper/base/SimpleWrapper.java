package de.polocloud.api.wrapper.base;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.base.IGameServer;
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
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

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
    private final ChannelHandlerContext ctx;

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
        sendPacket(new WrapperRequestShutdownPacket());
        return true;
    }

    @Override
    public void sendPacket(Packet packet) {

        Packet forwardingPacket = new ForwardingPacket(PoloType.WRAPPER, this.name, packet);
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
            PoloCloudAPI.getInstance().getConnection().getProtocol().firePacketHandlers(ctx, packet);
            return;
        }
        PoloCloudAPI.getInstance().getConnection().sendPacket(forwardingPacket);
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
