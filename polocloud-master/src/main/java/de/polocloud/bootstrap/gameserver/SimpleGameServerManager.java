package de.polocloud.bootstrap.gameserver;

import com.google.inject.Inject;
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

public class SimpleGameServerManager implements IGameServerManager {

    private List<IGameServer> gameServerList = new ArrayList<>();

    @Inject
    private MasterPubSubManager pubSubManager;

    @Override
    public CompletableFuture<IGameServer> getGameServerByName(String name) {

        for (IGameServer iGameServer : gameServerList) {
            if (iGameServer.getName().equalsIgnoreCase(name)) {
                return CompletableFuture.completedFuture(iGameServer);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<IGameServer> getGameSererBySnowflake(long snowflake) {

        for (IGameServer iGameServer : gameServerList) {
            if (iGameServer.getSnowflake() == snowflake) {
                return CompletableFuture.completedFuture(iGameServer);
            }
        }
        return CompletableFuture.completedFuture(null);
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

        List<IGameServer> result = new ArrayList<>();

        for (IGameServer iGameServer : gameServerList) {
            if (iGameServer.getTemplate().getName().equalsIgnoreCase(template.getName())) {
                result.add(iGameServer);
            }
        }
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletableFuture<List<IGameServer>> getGameServersByType(TemplateType type) {


        List<IGameServer> result = new ArrayList<>();

        for (IGameServer iGameServer : gameServerList) {
            if (iGameServer.getTemplate().getTemplateType().equals(type)) {
                result.add(iGameServer);
            }
        }
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public void registerGameServer(IGameServer gameServer) {
        gameServerList.add(gameServer);
    }

    @Override
    public void unregisterGameServer(IGameServer gameServer) {
        gameServerList.remove(gameServer);
        try {
            for (IGameServer proxyGameServer : getGameServersByType(TemplateType.PROXY).get()) {
                proxyGameServer.sendPacket(new GameServerUnregisterPacket(gameServer.getSnowflake(), gameServer.getName()));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        pubSubManager.publish("polo:event:serverStopped", gameServer.getName());


    }

    @Override
    public CompletableFuture<IGameServer> getGameServerByConnection(ChannelHandlerContext ctx) {

        try {
            for (IGameServer gameServer : getGameServers().get()) {
                if (gameServer.getStatus().equals(GameServerStatus.RUNNING) &&
                    ((SimpleGameServer) gameServer).getCtx().channel().id().asLongText().equalsIgnoreCase(ctx.channel().id().asLongText())) {
                    return CompletableFuture.completedFuture(gameServer);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(null);
    }
}
