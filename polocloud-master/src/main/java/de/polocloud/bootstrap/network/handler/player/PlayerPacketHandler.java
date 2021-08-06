package de.polocloud.bootstrap.network.handler.player;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.api.cloudplayer.APIRequestCloudPlayerPacket;
import de.polocloud.api.network.protocol.packet.api.fallback.APIRequestPlayerMoveFallbackPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerCloudCommandExecutePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerRequestJoinPacket;
import de.polocloud.api.network.protocol.packet.gameserver.permissions.PermissionCheckResponsePacket;
import de.polocloud.api.network.response.ResponseHandler;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.bootstrap.network.SimplePacketHandler;

public class PlayerPacketHandler extends PlayerPacketServiceController {

    @Inject
    public ICloudPlayerManager playerManager;

    public PlayerPacketHandler() {

        new SimplePacketHandler<GameServerCloudCommandExecutePacket>(GameServerCloudCommandExecutePacket.class, packet ->
            executeICloudPlayerCommand(packet, (cloudCommands, strings) -> getPossibleCommand(strings).forEach(it ->
                executeCommand(playerManager, it, packet.getUuid(), strings))));

        new SimplePacketHandler<APIRequestCloudPlayerPacket>(APIRequestCloudPlayerPacket.class, (ctx, packet) ->
            sendICloudPlayerAPIResponse(playerManager, ctx, packet));

        new SimplePacketHandler<APIRequestPlayerMoveFallbackPacket>(APIRequestPlayerMoveFallbackPacket.class, packet ->
            playerManager.getOnlinePlayer(packet.getPlayername()).thenAccept(player -> sendToFallback(player)));

        new SimplePacketHandler<PermissionCheckResponsePacket>(PermissionCheckResponsePacket.class, packet ->
            ResponseHandler.getCompletableFuture(packet.getRequest(), true).complete(packet.isResponse()));

        new SimplePacketHandler<GameServerPlayerRequestJoinPacket>(GameServerPlayerRequestJoinPacket.class,
            (ctx, packet) -> getSearchedFallback(packet, (iGameServers, uuid) -> {
                if (isGameServerListEmpty(iGameServers)) {
                    sendMasterPlayerRequestJoinResponsePacket(ctx, uuid, "", -1);
                    return;
                }
                IGameServer gameServer = getNextFallback(iGameServers);
                sendMasterPlayerRequestJoinResponsePacket(ctx, uuid, gameServer == null ? "" : gameServer.getName(), gameServer == null ? -1 : gameServer.getSnowflake());
            }));

    }
}
