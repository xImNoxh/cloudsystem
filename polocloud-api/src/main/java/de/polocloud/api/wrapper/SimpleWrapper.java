package de.polocloud.api.wrapper;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.ForwardingPacket;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerStartPacket;
import de.polocloud.api.network.protocol.packet.master.MasterRequestsServerTerminatePacket;
import de.polocloud.api.network.protocol.packet.master.MasterStartServerPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRequestShutdownPacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.TemplateType;
import de.polocloud.api.util.PoloUtils;
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
        List<IGameServer> gameServers = PoloUtils.sneakyThrows(() -> PoloCloudAPI.getInstance().getGameServerManager().getGameServers().get());
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
        gameServer.setPort(-1);

        sendPacket(new MasterStartServerPacket(gameServer, this));
    }

    @Override
    public void stopServer(IGameServer gameServer) {
        PoloCloudAPI.getInstance().getGameServerManager().unregisterGameServer(PoloUtils.sneakyThrows(() -> PoloCloudAPI.getInstance().getGameServerManager().getGameServerByName(gameServer.getName()).get()));
        sendPacket(new MasterRequestsServerTerminatePacket(gameServer));
    }
}
