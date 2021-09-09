package de.polocloud.api.gameserver;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.pool.ObjectPool;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.wrapper.ex.NoWrapperFoundException;
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
    IGameServer getCached(ChannelHandlerContext ctx);

    /**
     * Gets a collection of all loaded {@link IGameServer}s
     * that match a given {@link ITemplate}
     *
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     */
    default List<IGameServer> getAllCached(ITemplate template) {
        return this.getAllCached(iGameServer -> iGameServer.getTemplate().getName().equalsIgnoreCase(template.getName()));
    }

    /**
     * Gets a free port for a {@link ITemplate}
     *
     * @param template the template
     * @return port as int
     */
    int getFreePort(ITemplate template);

    /**
     * Gets a free id for a {@link ITemplate}
     *
     * @param template the template
     * @return id as int
     */
    int getFreeId(ITemplate template);

    /**
     * Gets a collection of all loaded {@link IGameServer}s
     * that match a given {@link TemplateType}
     *
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     */
    default List<IGameServer> getAllCached(TemplateType type) {
        return getAllCached(iGameServer -> iGameServer.getTemplate().getTemplateType().equals(type));
    }

    /**
     * Registers an {@link IGameServer} in cache
     *
     * @param gameServer the gameServer
     */
    void register(IGameServer gameServer);

    /**
     * Unregisters an {@link IGameServer} in cache
     *
     * @param gameServer the gameServer
     */
    void unregister(IGameServer gameServer);

    /**
     * Starts a new {@link IGameServer} from an {@link ITemplate}
     *
     * @param count the amount of servers
     * @param template the template
     */
    void startServer(ITemplate template, int count) throws NoWrapperFoundException;

    /**
     * Stops an {@link IGameServer}
     *
     * @param gameServer the server
     */
    void stopServer(IGameServer gameServer) throws NoWrapperFoundException;

    /**
     * Stops all {@link IGameServer}s of an {@link ITemplate}
     *
     * @param template the template
     */
    void stopServers(ITemplate template) throws NoWrapperFoundException;

    /**
     * Gets the current {@link IGameServer} if Spigot or Proxy-Instance
     * Otherwise it will return null
     *
     * @return current gameServer
     */
    IGameServer getThisService();

}
