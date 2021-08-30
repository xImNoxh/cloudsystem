package de.polocloud.bootstrap.network.handler.player;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.packets.api.cloudplayer.APIRequestCloudPlayerPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerRegisterPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUnregisterPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUpdatePacket;
import de.polocloud.api.network.packets.gameserver.GameServerCloudCommandExecutePacket;

import de.polocloud.api.network.packets.gameserver.permissions.PermissionCheckResponsePacket;
import de.polocloud.api.network.request.ResponseHandler;
import de.polocloud.api.network.request.base.component.PoloComponent;
import de.polocloud.api.network.request.base.other.IRequestHandler;
import de.polocloud.api.player.ICloudPlayer;
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

        new SimplePacketHandler<>(GameServerCloudCommandExecutePacket.class, packet ->
            executeICloudPlayerCommand(packet, (cloudCommands, strings) -> getPossibleCommand(strings).forEach(it ->
                executeCommand(playerManager, it, packet.getUuid(), strings))));

        new SimplePacketHandler<APIRequestCloudPlayerPacket>(APIRequestCloudPlayerPacket.class, (ctx, packet) ->
            sendICloudPlayerAPIResponse(playerManager, ctx, packet));

        new SimplePacketHandler<>(PermissionCheckResponsePacket.class, packet ->
            ResponseHandler.getCompletableFuture(packet.getRequest(), true).complete(packet.isResponse()));


        //TODO CALL SWITCH EVENT PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerSwitchServerEvent(cloudPlayer, to));
        new SimplePacketHandler<>(CloudPlayerRegisterPacket.class, packet -> {
            this.callConnectEvent(MasterPubSubManager.getInstance(), packet.getCloudPlayer());
            this.sendConnectMessage(masterConfig, packet.getCloudPlayer());
            
            Master.getInstance().getCloudPlayerManager().registerPlayer(packet.getCloudPlayer());
            Master.getInstance().updateCache();
        });

        new SimplePacketHandler<>(CloudPlayerUnregisterPacket.class, packet -> {

            this.sendDisconnectMessage(masterConfig, packet.getCloudPlayer());
            this.callDisconnectEvent(pubSubManager, packet.getCloudPlayer());
            
            Master.getInstance().getCloudPlayerManager().unregisterPlayer(packet.getCloudPlayer());
            Master.getInstance().updateCache();
            
        });

        new SimplePacketHandler<>(CloudPlayerUpdatePacket.class, packet -> {
            Master.getInstance().getCloudPlayerManager().updateObject(packet.getCloudPlayer());
            Master.getInstance().updateCache();
        });

    }
}
