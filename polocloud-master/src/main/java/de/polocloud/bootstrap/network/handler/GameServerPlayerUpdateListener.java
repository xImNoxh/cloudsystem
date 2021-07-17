package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerUpdatePacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.player.SimpleCloudPlayer;
import de.polocloud.bootstrap.pubsub.MasterPubSubManager;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class GameServerPlayerUpdateListener extends IPacketHandler {

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private ICloudPlayerManager playerManager;

    @Inject
    private MasterConfig masterConfig;
    @Inject
    private MasterPubSubManager pubSubManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        GameServerPlayerUpdatePacket packet = (GameServerPlayerUpdatePacket) obj;

        String name = packet.getName();
        UUID uuid = packet.getUuid();
        String targetServerSnowflake = packet.getTargetServer();

        try {
            IGameServer targetServer = gameServerManager.getGameServerByName(targetServerSnowflake).get();
            IGameServer proxyServer = gameServerManager.getGameServerByConnection(ctx).get();


            ICloudPlayer cloudPlayer;

            if (playerManager.isPlayerOnline(uuid).get()) {
                cloudPlayer = playerManager.getOnlinePlayer(uuid).get();
            } else {
                cloudPlayer = new SimpleCloudPlayer(name, uuid);
                ((SimpleCloudPlayer) cloudPlayer).setProxyGameServer(proxyServer);
                cloudPlayer.getProxyServer().getCloudPlayers().add(cloudPlayer);
                playerManager.register(cloudPlayer);
            }

            if (cloudPlayer.getMinecraftServer() != null) {
                cloudPlayer.getMinecraftServer().getCloudPlayers().remove(cloudPlayer);
            }


            ((SimpleCloudPlayer) cloudPlayer).setMinecraftGameServer(targetServer);
            targetServer.getCloudPlayers().add(cloudPlayer);

            pubSubManager.publish("polo:event:serverUpdated", targetServer.getName());

            if (masterConfig.getProperties().isLogPlayerConnections())
                Logger.log(LoggerType.INFO, "Player " + ConsoleColors.CYAN.getAnsiCode() + name + ConsoleColors.GRAY.getAnsiCode() +
                    " is playing on " + targetServer.getName() + "(" + proxyServer.getName() + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return GameServerPlayerUpdatePacket.class;
    }
}
