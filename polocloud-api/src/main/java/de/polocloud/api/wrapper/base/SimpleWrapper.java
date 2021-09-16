package de.polocloud.api.wrapper.base;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.packets.gameserver.GameServerUnregisterPacket;
import de.polocloud.api.network.packets.master.MasterRequestServerStartPacket;
import de.polocloud.api.network.packets.master.MasterStopServerPacket;
import de.polocloud.api.network.protocol.packet.base.other.ForwardingPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.master.MasterRequestsServerTerminatePacket;
import de.polocloud.api.network.packets.master.MasterStartServerPacket;
import de.polocloud.api.network.packets.wrapper.WrapperRequestShutdownPacket;
import de.polocloud.api.player.ICloudPlayer;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedList;
import java.util.List;

public class SimpleWrapper implements IWrapper {

    private final String name;
    private final long snowflake;
    private final ChannelHandlerContext ctx;

    public SimpleWrapper(String name, long snowflake, ChannelHandlerContext ctx) {
        this.name = name;
        this.snowflake = snowflake;
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
        packet = new ForwardingPacket(PoloType.WRAPPER, this.name, packet);
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
            PoloCloudAPI.getInstance().getConnection().getProtocol().firePacketHandlers(ctx, packet);
            return;
        }
        PoloCloudAPI.getInstance().getConnection().sendPacket(packet);
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
    public long getSnowflake() {
        return this.snowflake;
    }

    @Override
    public ChannelHandlerContext ctx() {
        return ctx;
    }

    @Override
    public void startServer(IGameServer gameServer) {
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
