package de.polocloud.api.network.client;

import de.polocloud.api.network.INetworkConnection;

import java.util.function.Consumer;

/**
 * This interface does not implement any other methods
 * than the {@link INetworkConnection} does, but it's just for clarification
 *
 * if you want to identify your {@link INetworkConnection} instance you
 * can check if the connection is an instance of this {@link INettyClient}
 * to be sure it's a client or whatever you want
 */
public interface INettyClient extends INetworkConnection {

    void start(Consumer<INettyClient> consumer);
}
