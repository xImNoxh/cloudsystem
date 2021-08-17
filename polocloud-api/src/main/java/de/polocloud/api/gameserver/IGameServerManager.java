package de.polocloud.api.gameserver;

import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.TemplateType;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IGameServerManager {

    CompletableFuture<IGameServer> getGameServerByName(String name);

    CompletableFuture<IGameServer> getGameSererBySnowflake(long snowflake);

    CompletableFuture<List<IGameServer>> getGameServers();

    CompletableFuture<List<IGameServer>> getGameServersByTemplate(ITemplate template);

    CompletableFuture<List<IGameServer>> getGameServersByType(TemplateType type);

    void registerGameServer(IGameServer gameServer);

    void unregisterGameServer(IGameServer gameServer);

    CompletableFuture<IGameServer> getGameServerByConnection(ChannelHandlerContext ctx);
}
