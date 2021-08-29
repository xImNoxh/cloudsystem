package de.polocloud.api.wrapper.base;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.helper.ITerminatable;
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
     * Checks if the wrapper is (still) connected
     * to the master instance or not
     */
    boolean isStillConnected();

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
