package de.polocloud.bootstrap.gameserver;

import com.google.inject.Inject;
import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.event.gameserver.CloudGameServerStatusChangeEvent;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.api.PublishPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerUnregisterPacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.pubsub.MasterPubSubManager;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SimpleGameServerManager implements IGameServerManager {

    private List<IGameServer> gameServerList = new ArrayList<>();

    @Inject
    private MasterPubSubManager pubSubManager;

    @Override
    public CompletableFuture<IGameServer> getGameServerByName(String name) {
        return CompletableFuture.completedFuture(gameServerList.stream().filter(key -> key.getName().equalsIgnoreCase(name)).findAny().orElse(null));
    }

    @Override
    public CompletableFuture<IGameServer> getGameSererBySnowflake(long snowflake) {
        return CompletableFuture.completedFuture(gameServerList.stream().filter(key -> key.getSnowflake() == snowflake).findAny().orElse(null));
    }

    @Override
    public CompletableFuture<List<IGameServer>> getGameServers() {
        return CompletableFuture.completedFuture(this.gameServerList);
    }

    @Override
    public CompletableFuture<List<IGameServer>> getGameServersByTemplate(ITemplate template) {
        if (template == null) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(gameServerList.stream().filter(key ->
            key.getTemplate().getName().equalsIgnoreCase(template.getName())).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<IGameServer>> getGameServersByType(TemplateType type) {
        return CompletableFuture.completedFuture(gameServerList.stream().filter(key -> key.getTemplate().getTemplateType().equals(type)).collect(Collectors.toList()));
    }

    @Override
    public void registerGameServer(IGameServer gameServer) {
        gameServerList.add(gameServer);
    }

    @Override
    public void unregisterGameServer(IGameServer gameServer) {
        gameServerList.remove(gameServer);
        try {
            getGameServersByType(TemplateType.PROXY).get().forEach(it -> it.sendPacket(new GameServerUnregisterPacket(gameServer.getSnowflake(), gameServer.getName())));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        pubSubManager.publish("polo:event:serverStopped", gameServer.getName());
        EventRegistry.fireEvent(new CloudGameServerStatusChangeEvent(gameServer, CloudGameServerStatusChangeEvent.Status.STOPPING));


    }

    @Override
    public CompletableFuture<IGameServer> getGameServerByConnection(ChannelHandlerContext ctx) {
        try {
            return  CompletableFuture.completedFuture(getGameServers().get().stream().filter(key -> key.getStatus().equals(GameServerStatus.RUNNING) &&
                ((SimpleGameServer) key).getCtx().channel().id().asLongText().equalsIgnoreCase(ctx.channel().id().asLongText())).findAny().orElse(null));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(null);
    }
}
