package de.polocloud.bootstrap.network.handler.player;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.console.ConsoleColors;
import de.polocloud.api.event.impl.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.impl.player.CloudPlayerJoinNetworkEvent;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerRegisterPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUnregisterPacket;
import de.polocloud.api.network.packets.cloudplayer.CloudPlayerUpdatePacket;

import de.polocloud.api.network.packets.gameserver.GameServerExecuteCommandPacket;
import de.polocloud.api.network.packets.gameserver.proxy.ProxyTablistUpdatePacket;
import de.polocloud.api.network.packets.master.MasterPlayerKickPacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendComponentPacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendMessagePacket;
import de.polocloud.api.network.packets.master.MasterPlayerSendToServerPacket;
import de.polocloud.api.network.packets.other.RequestPassOnPacket;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import de.polocloud.api.network.protocol.packet.base.response.def.Request;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.network.SimplePacketHandler;

import java.util.function.Consumer;

public class PlayerPacketHandler  {

    public PlayerPacketHandler() {

        new SimplePacketHandler<>(MasterPlayerSendMessagePacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));
        new SimplePacketHandler<>(MasterPlayerSendComponentPacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));
        new SimplePacketHandler<>(MasterPlayerKickPacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));
        new SimplePacketHandler<>(ProxyTablistUpdatePacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));
        new SimplePacketHandler<>(MasterPlayerSendToServerPacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));
        new SimplePacketHandler<>(GameServerExecuteCommandPacket.class, packet -> PoloCloudAPI.getInstance().sendPacket(packet));

        new SimplePacketHandler<>(CloudPlayerRegisterPacket.class, packet -> {
            PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerJoinNetworkEvent(packet.getCloudPlayer()));
            ICloudPlayer cloudPlayer = packet.getCloudPlayer();
            if (PoloCloudAPI.getInstance().getMasterConfig().getProperties().isLogPlayerConnections() && cloudPlayer != null && cloudPlayer.getProxyServer() != null)
                PoloLogger.print(LogLevel.INFO, "Player " + ConsoleColors.CYAN + cloudPlayer.getName() + ConsoleColors.GRAY +
                    " is connected on " + cloudPlayer.getProxyServer().getName() + "!");
            
            Master.getInstance().getCloudPlayerManager().register(packet.getCloudPlayer());
            Master.getInstance().updateCache();
        });

        new SimplePacketHandler<>(CloudPlayerUnregisterPacket.class, packet -> {

            if (PoloCloudAPI.getInstance().getMasterConfig().getProperties().isLogPlayerConnections()) PoloLogger.print(LogLevel.INFO, "Player " + ConsoleColors.CYAN + packet.getCloudPlayer().getName() + ConsoleColors.GRAY + " is now disconnected!");
            PoloCloudAPI.getInstance().getEventManager().fireEvent(new CloudPlayerDisconnectEvent(packet.getCloudPlayer()));
            
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
