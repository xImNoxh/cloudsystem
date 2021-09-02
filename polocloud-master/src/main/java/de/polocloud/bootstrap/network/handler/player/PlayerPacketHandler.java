package de.polocloud.bootstrap.network.handler.player;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.packets.api.cloudplayer.APIRequestCloudPlayerPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerRegisterPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUnregisterPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUpdatePacket;

import de.polocloud.api.network.packets.gameserver.GameServerExecuteCommandPacket;
import de.polocloud.api.network.packets.gameserver.permissions.PermissionCheckResponsePacket;
import de.polocloud.api.network.packets.gameserver.proxy.ProxyTablistUpdatePacket;
import de.polocloud.api.network.packets.master.MasterPlayerKickPacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendMessagePacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendToServerPacket;
import de.polocloud.api.network.request.PacketMessenger;
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
        new SimplePacketHandler<APIRequestCloudPlayerPacket>(APIRequestCloudPlayerPacket.class, (ctx, packet) ->
            sendICloudPlayerAPIResponse(playerManager, ctx, packet));

        new SimplePacketHandler<>(PermissionCheckResponsePacket.class, packet ->
            PacketMessenger.getCompletableFuture(packet.getRequest(), true).complete(packet.isResponse()));

        new SimplePacketHandler<>(MasterPlayerSendMessagePacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));
        new SimplePacketHandler<>(MasterPlayerKickPacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));
        new SimplePacketHandler<>(ProxyTablistUpdatePacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));
        new SimplePacketHandler<>(MasterPlayerSendToServerPacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));
        new SimplePacketHandler<>(GameServerExecuteCommandPacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));


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
