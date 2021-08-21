package de.polocloud.api.network.server;

import de.polocloud.api.network.INetworkConnection;
import io.netty.channel.Channel;

import java.util.List;

/**
 * This interface does not implement any other methods
 * than the {@link INetworkConnection} does, but it's just for clarification
 * if you want to identify your {@link INetworkConnection} instance you
 * can check if the connection is an instance of this {@link INettyServer}
 * to be sure it's a server or whatever you want
 */
public interface INettyServer extends INetworkConnection {

    /**
     * Gets a list of all connected {@link Channel}s
     */
    List<Channel> getConnectedClients();
}
