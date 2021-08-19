package de.polocloud.api.wrapper;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.protocol.packet.IPacketSender;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public interface IWrapper extends IPacketSender, ITerminatable {

    /**
     * The name of this wrapper
     */
    String getName();

    /**
     * The identification id of this wrapper
     */
    long getSnowflake();

    /**
     * Gets a list of all {@link IGameServer}s running on this wrapper
     */
    List<IGameServer> getServers();

    /**
     * The netty context
     */
    ChannelHandlerContext ctx();

    /**
     * Starts an {@link IGameServer} on this wrapper
     *
     * @param gameServer the server
     */
    void startServer(IGameServer gameServer);

    /**
     * Stops an {@link IGameServer} on this wrapper
     */
    void stopServer(IGameServer gameServer);

}
