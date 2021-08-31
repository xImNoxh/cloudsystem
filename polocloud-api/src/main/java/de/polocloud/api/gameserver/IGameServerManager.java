package de.polocloud.api.gameserver;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.pool.ObjectPool;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.TemplateType;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IGameServerManager extends ObjectPool<IGameServer> {

    /**
     * Gets an {@link IGameServer} by its {@link ChannelHandlerContext}
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     *
     * @param ctx the channelHandlerContext
     */
    IGameServer getCachedObject(ChannelHandlerContext ctx);

    /**
     * Gets a collection of all loaded {@link IGameServer}s
     * that match a given {@link ITemplate}
     *
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     */
    default List<IGameServer> getGameServersByTemplate(ITemplate template) {
        return this.getAllCached(iGameServer -> iGameServer.getTemplate().getName().equalsIgnoreCase(template.getName()));
    }

    /**
     * Gets a collection of all loaded {@link IGameServer}s
     * that match a given {@link TemplateType}
     *
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     */
    default List<IGameServer> getGameServersByType(TemplateType type) {
        return getAllCached(iGameServer -> iGameServer.getTemplate().getTemplateType().equals(type));
    }

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
