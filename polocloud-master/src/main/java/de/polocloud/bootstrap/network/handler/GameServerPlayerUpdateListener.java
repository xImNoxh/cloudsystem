package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerUpdatePacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.player.SimpleCloudPlayer;
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

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {
        GameServerPlayerUpdatePacket packet = (GameServerPlayerUpdatePacket) obj;

        String name = packet.getName();
        UUID uuid = packet.getUuid();
        String targetServerSnowflake = packet.getTargetServer();

        IGameServer targetServer = gameServerManager.getGameServerByName(targetServerSnowflake);
        IGameServer proxyServer = gameServerManager.getGameServerByConnection(ctx);

        ICloudPlayer cloudPlayer;

        if (playerManager.isPlayerOnline(uuid)) {
            cloudPlayer = playerManager.getOnlinePlayer(uuid);
        } else {
            cloudPlayer = new SimpleCloudPlayer(name, uuid);
            ((SimpleCloudPlayer) cloudPlayer).setProxyGameServer(proxyServer);
            cloudPlayer.getProxyServer().getCloudPlayers().add(cloudPlayer);
            playerManager.register(cloudPlayer);
        }

        if(cloudPlayer.getMinecraftServer() != null){
            cloudPlayer.getMinecraftServer().getCloudPlayers().remove(cloudPlayer);
        }


        ((SimpleCloudPlayer) cloudPlayer).setMinecraftGameServer(targetServer);
        targetServer.getCloudPlayers().add(cloudPlayer);

        if(masterConfig.getProperties().isLogPlayerConnections())
        Logger.log(LoggerType.INFO, "Player " + ConsoleColors.CYAN.getAnsiCode() + name + ConsoleColors.GRAY.getAnsiCode() +
            " is playing on " +  targetServer.getName() + "(" + proxyServer.getName() + ")");
    }

    @Override
    public Class<? extends IPacket> getPacketClass() {
        return GameServerPlayerUpdatePacket.class;
    }
}
