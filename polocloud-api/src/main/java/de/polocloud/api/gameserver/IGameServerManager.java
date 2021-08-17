package de.polocloud.api.gameserver;

import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.TemplateType;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IGameServerManager {

    /**
     * Gets an {@link IGameServer} by its name
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     *
     * @param name the name of the server
     */
    CompletableFuture<IGameServer> getGameServerByName(String name);

    /**
     * Gets an {@link IGameServer} by its {@link ChannelHandlerContext}
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     *
     * @param ctx the channelHandlerContext
     */
    CompletableFuture<IGameServer> getGameServerByConnection(ChannelHandlerContext ctx);

    /**
     * Gets an {@link IGameServer} by its snowflake
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     *
     * @param snowflake the name of the snowflake
     */
    CompletableFuture<IGameServer> getGameSererBySnowflake(long snowflake);

    /**
     * Gets a collection of all loaded {@link IGameServer}s
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     */
    CompletableFuture<List<IGameServer>> getGameServers();

    /**
     * Gets a collection of all loaded {@link IGameServer}s
     * that match a given {@link ITemplate}
     *
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     */
    CompletableFuture<List<IGameServer>> getGameServersByTemplate(ITemplate template);

    /**
     * Gets a collection of all loaded {@link IGameServer}s
     * that match a given {@link TemplateType}
     *
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     */
    CompletableFuture<List<IGameServer>> getGameServersByType(TemplateType type);

    /**
     * Registers an {@link IGameServer} in cache
     *
     * @param gameServer the gameServer
     */
    void registerGameServer(IGameServer gameServer);

    /**
     * Unregisters an {@link IGameServer} in cache
     *
     * @param gameServer the gameServer
     */
    void unregisterGameServer(IGameServer gameServer);

    /**
     * Gets the current {@link IGameServer} if Spigot or Proxy-Instance
     * Otherwise it will return null
     *
     * @return current gameServer
     */
    IGameServer getThisService();
}
