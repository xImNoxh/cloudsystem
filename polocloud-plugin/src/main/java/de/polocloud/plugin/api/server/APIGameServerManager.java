package de.polocloud.plugin.api.server;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.api.APIRequestGameServerPacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.TemplateType;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.api.response.ResponseHandler;
import de.polocloud.plugin.protocol.NetworkClient;
import io.netty.channel.ChannelHandlerContext;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class APIGameServerManager implements IGameServerManager {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final NetworkClient networkClient = CloudPlugin.getInstance().getNetworkClient();

    @Override
    public CompletableFuture<IGameServer> getGameServerByName(String name) {
        CompletableFuture<IGameServer> completableFuture = new CompletableFuture<>();

        executor.execute(() -> {
            UUID requestID = UUID.randomUUID();

            ResponseHandler.register(requestID, completableFuture);
            networkClient.sendPacket(new APIRequestGameServerPacket(requestID, APIRequestGameServerPacket.Action.NAME, name));

        });

        return completableFuture;
    }

    @Override
    public CompletableFuture<IGameServer> getGameSererBySnowflake(long snowflake) {
        CompletableFuture<IGameServer> completableFuture = new CompletableFuture<>();

        executor.execute(() -> {
            UUID requestId = UUID.randomUUID();

            ResponseHandler.register(requestId, completableFuture);
            networkClient.sendPacket(new APIRequestGameServerPacket(requestId, APIRequestGameServerPacket.Action.SNOWFLAKE, "_"));
        });


        return completableFuture;
    }

    @Override
    public CompletableFuture<List<IGameServer>> getGameServers() {
        CompletableFuture<List<IGameServer>> completableFuture = new CompletableFuture<>();

        executor.execute(() -> {
            UUID requestId = UUID.randomUUID();

            ResponseHandler.register(requestId, completableFuture);
            networkClient.sendPacket(new APIRequestGameServerPacket(requestId, APIRequestGameServerPacket.Action.ALL, "_"));
        });


        return completableFuture;
    }

    @Override
    public CompletableFuture<List<IGameServer>> getGameServersByTemplate(ITemplate template) {
        CompletableFuture<List<IGameServer>> completableFuture = new CompletableFuture<>();

        executor.execute(() -> {
            UUID requestId = UUID.randomUUID();

            ResponseHandler.register(requestId, completableFuture);
            networkClient.sendPacket(new APIRequestGameServerPacket(requestId, APIRequestGameServerPacket.Action.LIST_BY_TEMPLATE, template.getName()));
        });


        return completableFuture;
    }

    @Override
    public CompletableFuture<List<IGameServer>> getGameServersByType(TemplateType type) {
        CompletableFuture<List<IGameServer>> completableFuture = new CompletableFuture<>();

        executor.execute(() -> {
            UUID requestId = UUID.randomUUID();

            ResponseHandler.register(requestId, completableFuture);
            networkClient.sendPacket(new APIRequestGameServerPacket(requestId, APIRequestGameServerPacket.Action.LIST_BY_TYPE, type.toString()));
        });


        return completableFuture;

    }

    @Override
    public void registerGameServer(IGameServer gameServer) {
        throw new NotImplementedException();
    }

    @Override
    public void unregisterGameServer(IGameServer gameServer) {
        throw new NotImplementedException();
    }

    @Override
    public CompletableFuture<IGameServer> getGameServerByConnection(ChannelHandlerContext ctx) {
        throw new NotImplementedException();
    }
}
