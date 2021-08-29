package de.polocloud.api.wrapper.base;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.protocol.packet.base.other.ForwardingPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.master.MasterRequestsServerTerminatePacket;
import de.polocloud.api.network.packets.master.MasterStartServerPacket;
import de.polocloud.api.network.packets.wrapper.WrapperRequestShutdownPacket;
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
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
            PoloCloudAPI.getInstance().getConnection().getProtocol().firePacketHandlers(ctx, packet);
            return;
        }
        PoloCloudAPI.getInstance().getConnection().sendPacket(new ForwardingPacket(PoloType.WRAPPER, this.name, packet));
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
        PoloCloudAPI.getInstance().getGameServerManager().registerGameServer(gameServer);
        gameServer.setPort(-1);

        sendPacket(new MasterStartServerPacket(gameServer, this));
    }

    @Override
    public void stopServer(IGameServer gameServer) {
        PoloCloudAPI.getInstance().getGameServerManager().unregisterGameServer(PoloCloudAPI.getInstance().getGameServerManager().getCached(gameServer.getName()));
        sendPacket(new MasterRequestsServerTerminatePacket(gameServer));
    }
}
