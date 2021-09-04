package de.polocloud.plugin.protocol.register;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.event.SimpleCachedEventManager;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.port.IPortManager;
import de.polocloud.api.gameserver.port.SimpleCachedPortManager;
import de.polocloud.api.network.packets.api.EventPacket;
import de.polocloud.api.network.packets.api.GlobalCachePacket;
import de.polocloud.api.network.packets.api.MasterCache;
import de.polocloud.api.network.packets.api.PropertyCachePacket;
import de.polocloud.api.network.packets.gameserver.GameServerExecuteCommandPacket;
import de.polocloud.api.network.packets.gameserver.GameServerShutdownPacket;
import de.polocloud.api.network.packets.gameserver.GameServerUpdatePacket;
import de.polocloud.api.network.packets.master.MasterPlayerKickPacket;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import de.polocloud.api.network.protocol.packet.base.response.ResponseState;
import de.polocloud.api.network.protocol.packet.base.response.def.Response;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.def.SimpleCachedPropertyManager;

import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.plugin.bootstrap.IBootstrap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NetworkPluginRegister {

    public NetworkPluginRegister(IBootstrap bootstrap) {

        new SimplePacketRegister<GlobalCachePacket>(GlobalCachePacket.class, globalCachePacket -> {
            MasterCache masterCache = globalCachePacket.getMasterCache();
            PoloCloudAPI.getInstance().setCache(masterCache);

            IPortManager portManager = globalCachePacket.getPortManager();
            PoloCloudAPI.getInstance().getPortManager().setProxyPort(((SimpleCachedPortManager)portManager).getProxyPort());
            PoloCloudAPI.getInstance().getPortManager().setServerPort(((SimpleCachedPortManager)portManager).getServerPort());

            ((SimpleCachedPortManager) PoloCloudAPI.getInstance().getPortManager()).setPortList(((SimpleCachedPortManager)portManager).getPortList());
            ((SimpleCachedPortManager) PoloCloudAPI.getInstance().getPortManager()).setProxyPortList(((SimpleCachedPortManager)portManager).getProxyPortList());
        });

        new SimplePacketRegister<PropertyCachePacket>(PropertyCachePacket.class, propertyCachePacket -> {
            Map<UUID, List<IProperty>> properties = propertyCachePacket.getProperties();
            ((SimpleCachedPropertyManager)PoloCloudAPI.getInstance().getPropertyManager()).setProperties(properties);
        });

        new SimplePacketRegister<GameServerShutdownPacket>(GameServerShutdownPacket.class, packet -> PoloCloudAPI.getInstance().getPoloBridge().shutdown());

        new SimplePacketRegister<MasterPlayerKickPacket>(MasterPlayerKickPacket.class, packet -> PoloCloudAPI.getInstance().getPoloBridge().kickPlayer(packet.getUuid(), packet.getMessage()));

        new SimplePacketRegister<GameServerExecuteCommandPacket>(GameServerExecuteCommandPacket.class, packet -> {
            if (PoloCloudAPI.getInstance().getGameServerManager().getThisService() != null && PoloCloudAPI.getInstance().getGameServerManager().getThisService().getName().equalsIgnoreCase(packet.getServer())) {
                PoloCloudAPI.getInstance().getPoloBridge().executeCommand(packet.getCommand());
            }
        });

        new SimplePacketRegister<GameServerUpdatePacket>(GameServerUpdatePacket.class, packet -> {
            PoloCloudAPI.getInstance().getGameServerManager().updateObject(packet.getGameServer());
        });


        new SimplePacketRegister<EventPacket>(EventPacket.class, eventPacket -> {

            Runnable runnable = () -> {
                if (Arrays.asList(eventPacket.getIgnoredTypes()).contains(PoloCloudAPI.getInstance().getType())) {
                    return;
                }
                IGameServer thisService = PoloCloudAPI.getInstance().getGameServerManager().getThisService();
                if (thisService != null && eventPacket.getExcept().equalsIgnoreCase(thisService.getName())) {
                    return;
                }

                PoloCloudAPI.getInstance().getEventManager().fireEvent(eventPacket.getEvent());
            };

            if (eventPacket.isAsync()) {
                Scheduler.runtimeScheduler().async().schedule(runnable);
            } else {
                runnable.run();
            }
        });


        PacketMessenger.registerHandler(request -> {
            if (request.getKey().equalsIgnoreCase("player-permission-check")) {
                JsonData data = request.getData();
                UUID uniqueId = UUID.fromString(data.getString("uniqueId"));
                String permission = data.getString("permission");

                if (!PoloCloudAPI.getInstance().getPoloBridge().isPlayerOnline(uniqueId)) {
                    request.respond(ResponseState.NULL);
                    return;
                }

                request.respond(new Response(new JsonData("has", PoloCloudAPI.getInstance().getPoloBridge().hasPermission(uniqueId, permission)), ResponseState.SUCCESS));
            }
        });

    }
}
