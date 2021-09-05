package de.polocloud.plugin.protocol;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.bridge.PoloPluginBridge;
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
import de.polocloud.api.network.protocol.packet.base.response.def.Request;
import de.polocloud.api.network.protocol.packet.base.response.def.Response;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.def.SimpleCachedPropertyManager;

import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.plugin.bootstrap.IBootstrap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class NetworkPluginRegister {

    public NetworkPluginRegister(IBootstrap bootstrap) {

        PoloCloudAPI.getInstance().registerSimplePacketHandler(GlobalCachePacket.class, globalCachePacket -> {
            MasterCache masterCache = globalCachePacket.getMasterCache();
            PoloCloudAPI.getInstance().setCache(masterCache);

            IPortManager portManager = globalCachePacket.getPortManager();
            PoloCloudAPI.getInstance().getPortManager().setProxyPort(((SimpleCachedPortManager)portManager).getProxyPort());
            PoloCloudAPI.getInstance().getPortManager().setServerPort(((SimpleCachedPortManager)portManager).getServerPort());

            ((SimpleCachedPortManager) PoloCloudAPI.getInstance().getPortManager()).setPortList(((SimpleCachedPortManager)portManager).getPortList());
            ((SimpleCachedPortManager) PoloCloudAPI.getInstance().getPortManager()).setProxyPortList(((SimpleCachedPortManager)portManager).getProxyPortList());
        });

        PoloCloudAPI.getInstance().registerSimplePacketHandler(PropertyCachePacket.class, propertyCachePacket -> {
            Map<UUID, List<IProperty>> properties = propertyCachePacket.getProperties();
            ((SimpleCachedPropertyManager)PoloCloudAPI.getInstance().getPropertyManager()).setProperties(properties);
        });

        PoloCloudAPI.getInstance().registerSimplePacketHandler(GameServerShutdownPacket.class, packet -> PoloCloudAPI.getInstance().getPoloBridge().shutdown());

        PoloCloudAPI.getInstance().registerSimplePacketHandler(MasterPlayerKickPacket.class, packet -> PoloCloudAPI.getInstance().getPoloBridge().kickPlayer(packet.getUuid(), packet.getMessage()));

        PoloCloudAPI.getInstance().registerSimplePacketHandler(GameServerExecuteCommandPacket.class, packet -> {
            if (PoloCloudAPI.getInstance().getGameServerManager().getThisService() != null && PoloCloudAPI.getInstance().getGameServerManager().getThisService().getName().equalsIgnoreCase(packet.getServer())) {
                PoloCloudAPI.getInstance().getPoloBridge().executeCommand(packet.getCommand());
            }
        });

        PoloCloudAPI.getInstance().registerSimplePacketHandler(GameServerUpdatePacket.class, packet -> {
            PoloCloudAPI.getInstance().getGameServerManager().updateObject(packet.getGameServer());
        });

        PacketMessenger.registerHandler(request -> {
            if (request.getKey().equalsIgnoreCase("player-ping")) {
                JsonData data = request.getData();
                UUID uniqueId = data.getUniqueId("uniqueId");

                PoloPluginBridge poloBridge = PoloCloudAPI.getInstance().getPoloBridge();
                if (poloBridge.isPlayerOnline(uniqueId)) {
                    request.respond(new Response(new JsonData("ping", poloBridge.getPing(uniqueId)), ResponseState.SUCCESS));
                } else {
                    request.respond(ResponseState.NULL);
                }
            }
        });

        PoloCloudAPI.getInstance().registerSimplePacketHandler(EventPacket.class, eventPacket -> {

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
