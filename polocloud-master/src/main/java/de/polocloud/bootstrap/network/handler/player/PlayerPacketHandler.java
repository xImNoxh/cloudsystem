package de.polocloud.bootstrap.network.handler.player;

import com.google.inject.Inject;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.api.cloudplayer.APIRequestCloudPlayerPacket;
import de.polocloud.api.network.protocol.packet.api.fallback.APIRequestPlayerMoveFallbackPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerCloudCommandExecutePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerDisconnectPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerRequestJoinPacket;
import de.polocloud.api.network.protocol.packet.gameserver.permissions.PermissionCheckResponsePacket;
import de.polocloud.api.network.protocol.packet.master.MasterUpdatePlayerInfoPacket;
import de.polocloud.api.network.response.ResponseHandler;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.network.SimplePacketHandler;
import de.polocloud.bootstrap.pubsub.MasterPubSubManager;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

public class PlayerPacketHandler extends PlayerPacketServiceController {

    @Inject
    public ICloudPlayerManager playerManager;

    @Inject
    public IGameServerManager serverManager;

    @Inject
    public MasterConfig masterConfig;

    @Inject
    private MasterPubSubManager pubSubManager;

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

        new SimplePacketHandler<GameServerPlayerDisconnectPacket>(GameServerPlayerDisconnectPacket.class, (ctx, packet) -> {
            getOnlinePlayer(packet, packet.getUuid(), playerManager, cloudPlayer -> {
                removeOnServerIfExist(playerManager, cloudPlayer);
                sendDisconnectMessage(masterConfig, packet);
                callDisconnectEvent(pubSubManager, cloudPlayer);
                updateProxyInfoService(serverManager,playerManager);
            });
        });

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
