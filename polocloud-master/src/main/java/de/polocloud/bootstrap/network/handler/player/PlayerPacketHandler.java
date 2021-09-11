package de.polocloud.bootstrap.network.handler.player;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerRegisterPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUnregisterPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUpdatePacket;

import de.polocloud.api.network.packets.gameserver.GameServerExecuteCommandPacket;
import de.polocloud.api.network.packets.gameserver.proxy.ProxyTablistUpdatePacket;
import de.polocloud.api.network.packets.master.MasterPlayerKickPacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendMessagePacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendToServerPacket;
import de.polocloud.api.network.packets.other.RequestPassOnPacket;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import de.polocloud.api.network.protocol.packet.base.response.def.Request;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.network.SimplePacketHandler;
import de.polocloud.bootstrap.pubsub.MasterPubSubManager;

import java.util.function.Consumer;

public class PlayerPacketHandler extends PlayerPacketServiceController {

    public PlayerPacketHandler() {

        new SimplePacketHandler<>(MasterPlayerSendMessagePacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));
        new SimplePacketHandler<>(MasterPlayerKickPacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));
        new SimplePacketHandler<>(ProxyTablistUpdatePacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));
        new SimplePacketHandler<>(MasterPlayerSendToServerPacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));
        new SimplePacketHandler<>(GameServerExecuteCommandPacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));

        new SimplePacketHandler<>(CloudPlayerRegisterPacket.class, packet -> {
            this.callConnectEvent(MasterPubSubManager.getInstance(), packet.getCloudPlayer());
            this.sendConnectMessage(PoloCloudAPI.getInstance().getMasterConfig(), packet.getCloudPlayer());
            
            Master.getInstance().getCloudPlayerManager().register(packet.getCloudPlayer());
            Master.getInstance().updateCache();
        });

        new SimplePacketHandler<>(CloudPlayerUnregisterPacket.class, packet -> {

            this.sendDisconnectMessage(PoloCloudAPI.getInstance().getMasterConfig(), packet.getCloudPlayer());
            this.callDisconnectEvent(MasterPubSubManager.getInstance(), packet.getCloudPlayer());
            
            Master.getInstance().getCloudPlayerManager().unregister(packet.getCloudPlayer());
            Master.getInstance().updateCache();
            
        });

        new SimplePacketHandler<>(RequestPassOnPacket.class, packet -> {
            String key = packet.getKey();

            PacketMessenger.registerHandler(new Consumer<Request>() {
                @Override
                public void accept(Request request) {
                    if (request.getKey().equalsIgnoreCase(key)) {
                        PacketMessenger.create().blocking().addListener(request::respond).send(request);
                        PacketMessenger.unregisterHandler(this);
                    }
                }
            });
        });

        new SimplePacketHandler<>(CloudPlayerUpdatePacket.class, packet -> {
            Master.getInstance().getCloudPlayerManager().update(packet.getCloudPlayer());
            Master.getInstance().updateCache();
        });

    }
}
