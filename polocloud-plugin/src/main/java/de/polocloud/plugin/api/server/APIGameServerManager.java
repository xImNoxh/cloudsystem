package de.polocloud.plugin.api.server;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.api.APIRequestGameServerPacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.TemplateType;
import de.polocloud.api.util.Snowflake;
import de.polocloud.plugin.CloudPlugin;
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

    private final Map<UUID, CompletableFuture<IGameServer>> futureMap = new ConcurrentHashMap<>();

    public <T> CompletableFuture<T> getCompletableFuture(UUID requestId, boolean autoRemove) {
        if (autoRemove) {
            return (CompletableFuture<T>) futureMap.remove(requestId);
        } else {
            return (CompletableFuture<T>) futureMap.get(requestId);
        }
    }

    public void removeCompletableFuture(UUID requestId){
        futureMap.remove(requestId);
    }

    @Override
    public CompletableFuture<IGameServer> getGameServerByName(String name) {
        CompletableFuture<IGameServer> completableFuture = new CompletableFuture<>();


        executor.submit(() -> {
            UUID requestID = UUID.randomUUID();
            futureMap.put(requestID, completableFuture);

            networkClient.sendPacket(new APIRequestGameServerPacket(requestID, APIRequestGameServerPacket.Action.NAME, name));


        });

        return completableFuture;
    }

    @Override
    public CompletableFuture<IGameServer> getGameSererBySnowflake(long snowflake) {
        throw new NotImplementedException();

    }

    @Override
    public CompletableFuture<List<IGameServer>> getGameServers() {
        throw new NotImplementedException();

    }

    @Override
    public CompletableFuture<List<IGameServer>> getGameServersByTemplate(ITemplate template) {
        throw new NotImplementedException();

    }

    @Override
    public CompletableFuture<List<IGameServer>> getGameServersByType(TemplateType type) {
        throw new NotImplementedException();

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
