package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerDisconnectPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.pubsub.MasterPubSubManager;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GameServerPlayerDisconnectListener extends IPacketHandler {

    @Inject
    private ICloudPlayerManager playerManager;

    @Inject
    private MasterConfig masterConfig;

    @Inject
    private MasterPubSubManager pubSubManager;
    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {

        GameServerPlayerDisconnectPacket packet = (GameServerPlayerDisconnectPacket) obj;

        UUID uuid = packet.getUuid();

        ICloudPlayer onlinePlayer = null;
        try {
            onlinePlayer = playerManager.getOnlinePlayer(packet.getUuid()).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
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

        pubSubManager.publish("polo:event:playerQuit", uuid.toString());
        EventRegistry.fireEvent(new CloudPlayerDisconnectEvent(onlinePlayer));


    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return GameServerPlayerDisconnectPacket.class;
    }
}
