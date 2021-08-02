package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerRequestJoinPacket;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerRequestJoinResponsePacket;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.pubsub.MasterPubSubManager;
import io.netty.channel.ChannelHandlerContext;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class GameServerPlayerRequestJoinHandler extends IPacketHandler<Packet> {

    @Inject
    private IGameServerManager gameServerManager;
    @Inject
    private ITemplateService templateService;
    @Inject
    private ICloudPlayerManager playerManager;
    @Inject
    private MasterConfig config;

    @Inject
    private MasterPubSubManager pubSubManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        GameServerPlayerRequestJoinPacket packet = (GameServerPlayerRequestJoinPacket) obj;
        UUID uuid = packet.getUuid();
        List<IGameServer> searchedServer = Master.getInstance().getFallbackSearchService().searchForTemplate(null, false);
        if (searchedServer == null || searchedServer.isEmpty()) {
            ctx.writeAndFlush(new MasterPlayerRequestJoinResponsePacket(uuid, "", -1));
            return;
        }

        IGameServer gameServer = searchedServer.stream().max(Comparator.comparingInt(IGameServer::getOnlinePlayers)).orElse(null);

        if (gameServer == null) {
            ctx.writeAndFlush(new MasterPlayerRequestJoinResponsePacket(uuid, "", -1));
            return;
        }
        ctx.writeAndFlush(new MasterPlayerRequestJoinResponsePacket(uuid, gameServer.getName(), gameServer.getSnowflake()));
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return GameServerPlayerRequestJoinPacket.class;
    }
}
