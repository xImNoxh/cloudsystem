package de.polocloud.api.network;

import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.IPacketSender;
import de.polocloud.api.network.request.IRequestManager;

import java.net.InetSocketAddress;

public interface INetworkConnection extends IStartable, ITerminatable, IPacketSender {

    /**
     * The {@link IRequestManager} instance to manage all {@link de.polocloud.api.network.request.base.future.PoloFuture}s
     * for API-Requests and responses
     *
     * @return the manager instance
     */
    IRequestManager getRequestManager();

    /**
     * The {@link IProtocol} instance of this connection
     * to manage {@link de.polocloud.api.network.protocol.IPacketHandler}s
     *
     * @return the protocol instance
     */
    IProtocol getProtocol();

    /**
     * Gets the address the connection is bound or connected to
     * If...
     * 'SERVER/CLOUD' -> Will return the localhost address
     * 'BRIDGE/SPIGOT/PROXY' -> Will return the Master-Address
     *
     * @return address as {@link InetSocketAddress}
     */
    InetSocketAddress getConnectedAddress();
}
