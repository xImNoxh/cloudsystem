package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerDisconnectPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class GameServerPlayerDisconnectListener extends IPacketHandler {

    @Inject
    private ICloudPlayerManager playerManager;

    @Inject
    private MasterConfig masterConfig;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {

        GameServerPlayerDisconnectPacket packet = (GameServerPlayerDisconnectPacket) obj;

        UUID uuid = packet.getUuid();

        ICloudPlayer onlinePlayer = playerManager.getOnlinePlayer(packet.getUuid());
        if (onlinePlayer == null) {
            return;
        }

        if (onlinePlayer.getProxyServer() != null) {
            onlinePlayer.getProxyServer().getCloudPlayers().remove(onlinePlayer);
        }
        if (onlinePlayer.getMinecraftServer() != null) {
            onlinePlayer.getMinecraftServer().getCloudPlayers().remove(onlinePlayer);
        }

        playerManager.unregister(onlinePlayer);

        if(masterConfig.getProperties().isLogPlayerConnections())
        Logger.log(LoggerType.INFO, "Player " + ConsoleColors.CYAN.getAnsiCode() + packet.getName() + ConsoleColors.GRAY.getAnsiCode() + " is now disconnected!");
    }

    @Override
    public Class<? extends IPacket> getPacketClass() {
        return GameServerPlayerDisconnectPacket.class;
    }
}
