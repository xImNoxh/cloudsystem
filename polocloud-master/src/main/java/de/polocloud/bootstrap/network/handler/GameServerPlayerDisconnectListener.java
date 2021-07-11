package de.polocloud.bootstrap.network.handler;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerDisconnectPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class GameServerPlayerDisconnectListener extends IPacketHandler {

    @Inject
    private ICloudPlayerManager playerManager;

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {

        GameServerPlayerDisconnectPacket packet = (GameServerPlayerDisconnectPacket) obj;

        UUID uuid = packet.getUuid();

        ICloudPlayer onlinePlayer = playerManager.getOnlinePlayer(packet.getUuid());

        onlinePlayer.getProxyServer().getCloudPlayers().remove(onlinePlayer);
        onlinePlayer.getMinecraftServer().getCloudPlayers().remove(onlinePlayer);

        playerManager.unregister(onlinePlayer);

        System.out.println("Player " + uuid.toString() + " disconnected!");

    }

    @Override
    public Class<? extends IPacket> getPacketClass() {
        return GameServerPlayerDisconnectPacket.class;
    }
}
