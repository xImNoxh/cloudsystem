package de.polocloud.plugin.api.implementations;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.protocol.packet.base.other.ForwardingPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.master.MasterRequestsServerTerminatePacket;
import de.polocloud.api.network.packets.master.MasterStartServerPacket;
import de.polocloud.api.network.packets.wrapper.WrapperRequestShutdownPacket;
import de.polocloud.api.wrapper.base.IWrapper;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedList;
import java.util.List;

public class SimplePluginWrapper implements IWrapper {

    private final String name;
    private final long snowflake;

    public SimplePluginWrapper(String name, long snowflake) {
        this.name = name;
        this.snowflake = snowflake;
    }

    @Override
    public boolean terminate() {
        sendPacket(new WrapperRequestShutdownPacket());
        return true;
    }

    @Override
    public boolean isStillConnected() {
        return true;
    }

    @Override
    public void sendPacket(Packet packet) {
        PoloCloudAPI.getInstance().sendPacket(new ForwardingPacket(PoloType.WRAPPER, this.name, packet));
    }

    @Override
    public List<IGameServer> getServers() {
        List<IGameServer> list = new LinkedList<>();
        for (IGameServer gameServer : PoloCloudAPI.getInstance().getGameServerManager().getAllCached()) {
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
        return null;
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
