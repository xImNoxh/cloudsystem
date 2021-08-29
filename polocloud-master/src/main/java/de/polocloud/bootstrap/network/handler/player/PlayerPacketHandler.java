package de.polocloud.bootstrap.network.handler.player;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.packets.api.cloudplayer.APIRequestCloudPlayerPacket;
import de.polocloud.api.network.packets.api.fallback.APIRequestPlayerMoveFallbackPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerRegisterPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUnregisterPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUpdatePacket;
import de.polocloud.api.network.packets.gameserver.GameServerCloudCommandExecutePacket;
import de.polocloud.api.network.packets.gameserver.GameServerPlayerDisconnectPacket;
import de.polocloud.api.network.packets.gameserver.GameServerPlayerRequestJoinPacket;
import de.polocloud.api.network.packets.gameserver.permissions.PermissionCheckResponsePacket;
import de.polocloud.api.network.request.ResponseHandler;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.network.SimplePacketHandler;
import de.polocloud.bootstrap.pubsub.MasterPubSubManager;

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
            playerManager.getCached(packet.getPlayername()).thenAccept(player -> sendToFallback(player)));

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

        //TODO
        /*new SimplePacketHandler<GameServerPlayerUpdatePacket>(GameServerPlayerUpdatePacket.class, (ctx, packet) -> {
            String name = packet.getName();
            UUID uuid = packet.getUuid();
            callCurrentServices(serverManager, packet.getTargetServer(), (targetServer, proxyServer) -> {
                ICloudPlayer cloudPlayer = null;
                boolean isOnline = false;
                if (!playerManager.isPlayerOnline(uuid)) {
                    cloudPlayer = playerManager.getCachedObject(uuid);
                } else {
                    cloudPlayer = new de.polocloud.api.player.SimpleCloudPlayer(name, uuid);
                    ((de.polocloud.api.player.SimpleCloudPlayer) cloudPlayer).setProxyServer(proxyServer.getName());
                    cloudPlayer.getProxyServer().getCloudPlayers().add(cloudPlayer);
                    playerManager.registerPlayer(cloudPlayer);

                    callConnectEvent(pubSubManager, cloudPlayer);
                    updateProxyInfoService(serverManager, playerManager);

                }

                IGameServer from = cloudPlayer.getMinecraftServer();

                if (cloudPlayer.getMinecraftServer() != null) cloudPlayer.getMinecraftServer().getCloudPlayers().remove(cloudPlayer);

                ((de.polocloud.api.player.SimpleCloudPlayer) cloudPlayer).setMinecraftServer(targetServer.getName());
                targetServer.getCloudPlayers().add(cloudPlayer);

                IGameServer to = cloudPlayer.getMinecraftServer();

                if (isOnline) {
                    pubSubManager.publish("polo:event:serverUpdated", targetServer.getName());
                    if (from != null) pubSubManager.publish("polo:event:playerSwitch", name + "," + from.getName() + "," + to.getName());
                    PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerSwitchServerEvent(cloudPlayer, to));
                }
                sendConnectMessage(masterConfig, cloudPlayer);
            }, ctx);
        });*/

        new SimplePacketHandler<GameServerPlayerRequestJoinPacket>(GameServerPlayerRequestJoinPacket.class,
            (ctx, packet) -> getSearchedFallback(packet, (iFallback, uuid) -> {
                if (iFallback == null) {
                    sendMasterPlayerRequestJoinResponsePacket(ctx, uuid, "", -1);
                    return;
                }
                IGameServer gameServer = PoloCloudAPI.getInstance().getFallbackManager().getFallback(iFallback);
                sendMasterPlayerRequestJoinResponsePacket(ctx, uuid, gameServer == null ? "" : gameServer.getName(), gameServer == null ? -1 : gameServer.getSnowflake());
            }));

        new SimplePacketHandler<>(CloudPlayerRegisterPacket.class, packet -> {
            Master.getInstance().getCloudPlayerManager().registerPlayer(packet.getCloudPlayer());
            Master.getInstance().updateCache();
        });

        new SimplePacketHandler<>(CloudPlayerUnregisterPacket.class, packet -> {
            Master.getInstance().getCloudPlayerManager().unregisterPlayer(packet.getCloudPlayer());
            Master.getInstance().updateCache();
        });

        new SimplePacketHandler<>(CloudPlayerUpdatePacket.class, packet -> {
            Master.getInstance().getCloudPlayerManager().updateObject(packet.getCloudPlayer());
            Master.getInstance().updateCache();
        });

    }
}
