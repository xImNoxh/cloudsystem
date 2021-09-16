package de.polocloud.api.network.server;

import de.polocloud.api.common.PoloType;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.client.INettyClient;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.util.map.UniqueMap;
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

    /**
     * Sends a {@link Packet} to specific {@link Channel}s only
     *
     * @param packet the packet
     * @param channels the channels
     */
    void sendPacket(Packet packet, Channel... channels);

    /**
     * Gets a map of all connected clients
     */
    UniqueMap<PoloType, INettyClient> getClientsWithType();
}
