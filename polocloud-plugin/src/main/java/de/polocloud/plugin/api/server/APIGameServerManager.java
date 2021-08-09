package de.polocloud.plugin.api.server;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerPacket;
import de.polocloud.api.network.response.ResponseHandler;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.TemplateType;
import de.polocloud.plugin.CloudPlugin;
import io.netty.channel.ChannelHandlerContext;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class APIGameServerManager implements IGameServerManager {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public CompletableFuture<IGameServer> getGameServerByName(String name) {
        return (CompletableFuture<IGameServer>) sendGameServerData(new CompletableFuture<IGameServer>(),APIRequestGameServerPacket.Action.NAME, name);
    }

    @Override
    public CompletableFuture<IGameServer> getGameSererBySnowflake(long snowflake) {
        return (CompletableFuture<IGameServer>) sendGameServerData(new CompletableFuture<IGameServer>(), APIRequestGameServerPacket.Action.SNOWFLAKE, "_");
    }

    @Override
    public CompletableFuture<List<IGameServer>> getGameServers() {
        return (CompletableFuture<List<IGameServer>>) sendGameServerData(new CompletableFuture<List<IGameServer>>(), APIRequestGameServerPacket.Action.ALL, "_");

    }

    @Override
    public CompletableFuture<List<IGameServer>> getGameServersByTemplate(ITemplate template) {
        return (CompletableFuture<List<IGameServer>>) sendGameServerData(new CompletableFuture<List<IGameServer>>(), APIRequestGameServerPacket.Action.LIST_BY_TEMPLATE, template.getName());
    }

    @Override
    public CompletableFuture<List<IGameServer>> getGameServersByType(TemplateType type) {
        return (CompletableFuture<List<IGameServer>>) sendGameServerData(new CompletableFuture<List<IGameServer>>(), APIRequestGameServerPacket.Action.LIST_BY_TYPE, type.toString());
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

    public CompletableFuture<?> sendGameServerData(CompletableFuture<?> future, APIRequestGameServerPacket.Action action, String data){
        executor.execute(() -> {
            UUID requestId = UUID.randomUUID();
            ResponseHandler.register(requestId, future);
            CloudPlugin.getCloudPluginInstance().getNetworkClient().sendPacket(new APIRequestGameServerPacket(requestId, action, data));
        });
        return future;
    }

}
