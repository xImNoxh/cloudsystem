package de.polocloud.bootstrap.network.handler.player;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.event.impl.player.CloudPlayerSwitchServerEvent;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.api.cloudplayer.APIRequestCloudPlayerPacket;
import de.polocloud.api.network.protocol.packet.api.fallback.APIRequestPlayerMoveFallbackPacket;
import de.polocloud.api.network.protocol.packet.cloudplayer.CloudPlayerRegisterPacket;
import de.polocloud.api.network.protocol.packet.cloudplayer.CloudPlayerUnregisterPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerCloudCommandExecutePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerDisconnectPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerRequestJoinPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerPlayerUpdatePacket;
import de.polocloud.api.network.protocol.packet.gameserver.permissions.PermissionCheckResponsePacket;
import de.polocloud.api.network.request.ResponseHandler;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.network.SimplePacketHandler;
import de.polocloud.bootstrap.player.SimpleCloudPlayer;
import de.polocloud.bootstrap.pubsub.MasterPubSubManager;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

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

        new SimplePacketHandler<>(APIRequestPlayerMoveFallbackPacket.class, packet ->
            playerManager.getOnlinePlayer(packet.getPlayername()).thenAccept(player -> sendToFallback(player)));

        new SimplePacketHandler<>(PermissionCheckResponsePacket.class, packet ->
            ResponseHandler.getCompletableFuture(packet.getRequest(), true).complete(packet.isResponse()));

        new SimplePacketHandler<GameServerPlayerDisconnectPacket>(GameServerPlayerDisconnectPacket.class, (ctx, packet) -> {
            getOnlinePlayer(packet, packet.getUuid(), playerManager, cloudPlayer -> {
                removeOnServerIfExist(playerManager, cloudPlayer);
                sendDisconnectMessage(masterConfig, packet);
                callDisconnectEvent(pubSubManager, cloudPlayer);
                updateProxyInfoService(serverManager,playerManager);
            });
        });

        new SimplePacketHandler<GameServerPlayerUpdatePacket>(GameServerPlayerUpdatePacket.class, (ctx, packet) -> {
            String name = packet.getName();
            UUID uuid = packet.getUuid();
            callCurrentServices(serverManager, packet.getTargetServer(), (targetServer, proxyServer) -> {
                ICloudPlayer cloudPlayer = null;
                boolean isOnline = false;
                try {
                    if (isOnline = playerManager.isPlayerOnline(uuid).get()) {
                        cloudPlayer = playerManager.getOnlinePlayer(uuid).get();
                    } else {
                        cloudPlayer = new SimpleCloudPlayer(name, uuid);
                        ((SimpleCloudPlayer) cloudPlayer).setProxyGameServer(proxyServer);
                        cloudPlayer.getProxyServer().getCloudPlayers().add(cloudPlayer);
                        playerManager.register(cloudPlayer);

                        callConnectEvent(pubSubManager, cloudPlayer);
                        updateProxyInfoService(serverManager, playerManager);

                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                IGameServer from = cloudPlayer.getMinecraftServer();

                if (cloudPlayer.getMinecraftServer() != null) cloudPlayer.getMinecraftServer().getCloudPlayers().remove(cloudPlayer);

                ((SimpleCloudPlayer) cloudPlayer).setMinecraftGameServer(targetServer);
                targetServer.getCloudPlayers().add(cloudPlayer);

                IGameServer to = cloudPlayer.getMinecraftServer();

                if (isOnline) {
                    pubSubManager.publish("polo:event:serverUpdated", targetServer.getName());
                    if (from != null) pubSubManager.publish("polo:event:playerSwitch", name + "," + from.getName() + "," + to.getName());
                    PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerSwitchServerEvent(cloudPlayer, to));
                }
                sendConnectMessage(masterConfig, cloudPlayer);
            }, ctx);
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

        new SimplePacketHandler<CloudPlayerRegisterPacket>(CloudPlayerRegisterPacket.class, packet -> {
            Master.getInstance().getCloudPlayerManager().register(packet.getCloudPlayer());
        });

        new SimplePacketHandler<CloudPlayerUnregisterPacket>(CloudPlayerUnregisterPacket.class, packet -> {
            Master.getInstance().getCloudPlayerManager().unregister(packet.getCloudPlayer());
        });

    }
}
