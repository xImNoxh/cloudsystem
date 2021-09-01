package de.polocloud.plugin.protocol.register;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.packets.api.cloudplayer.APIResponseCloudPlayerPacket;
import de.polocloud.api.network.packets.api.gameserver.APIResponseGameServerPacket;
import de.polocloud.api.network.packets.api.other.GlobalCachePacket;
import de.polocloud.api.network.packets.api.other.MasterCache;
import de.polocloud.api.network.packets.api.other.PropertyCachePacket;
import de.polocloud.api.network.packets.api.template.APIResponseTemplatePacket;
import de.polocloud.api.network.packets.gameserver.GameServerExecuteCommandPacket;
import de.polocloud.api.network.packets.gameserver.GameServerShutdownPacket;
import de.polocloud.api.network.packets.master.MasterPlayerKickPacket;
import de.polocloud.api.network.request.ResponseHandler;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.def.SimpleCachedPropertyManager;
import de.polocloud.api.template.base.ITemplate;

import de.polocloud.plugin.bootstrap.IBootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NetworkPluginRegister {

    public NetworkPluginRegister(IBootstrap bootstrap) {

        new SimplePacketRegister<GlobalCachePacket>(GlobalCachePacket.class, globalCachePacket -> {
            MasterCache masterCache = globalCachePacket.getMasterCache();
            PoloCloudAPI.getInstance().setCache(masterCache);
        });

        new SimplePacketRegister<PropertyCachePacket>(PropertyCachePacket.class, propertyCachePacket -> {
            Map<UUID, List<IProperty>> properties = propertyCachePacket.getProperties();
            ((SimpleCachedPropertyManager)PoloCloudAPI.getInstance().getPropertyManager()).setProperties(properties);
        });

        new SimplePacketRegister<GameServerShutdownPacket>(GameServerShutdownPacket.class, packet -> PoloCloudAPI.getInstance().getPoloBridge().shutdown());

        new SimplePacketRegister<MasterPlayerKickPacket>(MasterPlayerKickPacket.class, packet -> PoloCloudAPI.getInstance().getPoloBridge().kickPlayer(packet.getUuid(), packet.getMessage()));

        new SimplePacketRegister<GameServerExecuteCommandPacket>(GameServerExecuteCommandPacket.class, packet -> PoloCloudAPI.getInstance().getPoloBridge().executeCommand(packet.getCommand()));

        new SimplePacketRegister<APIResponseCloudPlayerPacket>(APIResponseCloudPlayerPacket.class, packet -> {
            UUID requestId = packet.getRequestId();
            List<ICloudPlayer> response = packet.getResponse();
            CompletableFuture<Object> completableFuture = ResponseHandler.getCompletableFuture(requestId, true);
            if (packet.getType() == APIResponseCloudPlayerPacket.Type.SINGLE) {
                completableFuture.complete(response.get(0));
            } else if (packet.getType() == APIResponseCloudPlayerPacket.Type.LIST) {
                completableFuture.complete(response);
            } else if (packet.getType() == APIResponseCloudPlayerPacket.Type.BOOLEAN) {
                completableFuture.complete(!response.isEmpty());
            }
        });

        new SimplePacketRegister<APIResponseGameServerPacket>(APIResponseGameServerPacket.class, (ctx, packet) -> {
            UUID requestId = packet.getRequestId();
            List<IGameServer> tmp = packet.getResponse();
            List<IGameServer> response = new ArrayList<>();
            CompletableFuture<Object> completableFuture = ResponseHandler.getCompletableFuture(requestId, true);

            for (IGameServer gameserver : tmp) {

                response.add(new SimpleGameServer(gameserver.getName(), gameserver.getMotd(), gameserver.getServiceVisibility(),
                    gameserver.getStatus(), gameserver.getSnowflake(), gameserver.getPing(), gameserver.getStartTime(),
                    gameserver.getTotalMemory(), gameserver.getPort(), gameserver.getMaxPlayers(), gameserver.getTemplate().getName()));
            }
            completableFuture.complete((packet.getType() == APIResponseGameServerPacket.Type.SINGLE ? response.get(0) : response));
        });

        new SimplePacketRegister<APIResponseTemplatePacket>(APIResponseTemplatePacket.class, packet -> {
            List<ITemplate> response = new ArrayList<>(packet.getResponse());
            ResponseHandler.getCompletableFuture(packet.getRequestId(), true).complete(
                packet.getType() == APIResponseTemplatePacket.Type.SINGLE ? response.get(0) : response);
        });


    }
}
