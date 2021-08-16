package de.polocloud.plugin.protocol.register;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.protocol.packet.api.cloudplayer.APIResponseCloudPlayerPacket;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIResponseGameServerPacket;
import de.polocloud.api.network.protocol.packet.api.template.APIResponseTemplatePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerExecuteCommandPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerShutdownPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerUpdatePacket;
import de.polocloud.api.network.protocol.packet.master.MasterPlayerKickPacket;
import de.polocloud.api.network.response.ResponseHandler;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.api.server.SimpleGameServer;
import de.polocloud.plugin.bootstrap.IBootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class NetworkPluginRegister {

    public NetworkPluginRegister(IBootstrap bootstrap) {

        new SimplePacketRegister<GameServerUpdatePacket>(GameServerUpdatePacket.class, (ctx, packet) -> {
            IGameServer gameserver = packet.getGameServer();
            CloudPlugin.getCloudPluginInstance().setGameServer(new SimpleGameServer(gameserver.getName(), gameserver.getMotd(), gameserver.getServiceVisibility(),
                gameserver.getStatus(), gameserver.getSnowflake(), gameserver.getPing(), gameserver.getStartTime(),
                gameserver.getTotalMemory(), gameserver.getPort(), gameserver.getMaxPlayers(), gameserver.getTemplate(), gameserver.getCloudPlayers()));
        });

        new SimplePacketRegister<GameServerShutdownPacket>(GameServerShutdownPacket.class, packet -> {
            CloudPlugin.getCloudPluginInstance().getBootstrap().shutdown();
        });

        new SimplePacketRegister<MasterPlayerKickPacket>(MasterPlayerKickPacket.class, packet -> {
            bootstrap.kick(packet.getUuid(), packet.getMessage());
        });

        new SimplePacketRegister<GameServerExecuteCommandPacket>(GameServerExecuteCommandPacket.class, packet -> {
            bootstrap.executeCommand(packet.getCommand());
        });

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
                    gameserver.getTotalMemory(), gameserver.getPort(), gameserver.getMaxPlayers(), gameserver.getTemplate(), gameserver.getCloudPlayers()));
            }
            completableFuture.complete((packet.getType() == APIResponseGameServerPacket.Type.SINGLE ? response.get(0) : response));
        });

        new SimplePacketRegister<APIResponseTemplatePacket>(APIResponseTemplatePacket.class, packet -> {
            List<ITemplate> response = packet.getResponse().stream().collect(Collectors.toList());
            ResponseHandler.getCompletableFuture(packet.getRequestId(), true).complete(
                packet.getType() == APIResponseTemplatePacket.Type.SINGLE ? response.get(0) : response);
        });


    }
}
