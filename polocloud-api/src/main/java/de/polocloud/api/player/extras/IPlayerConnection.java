package de.polocloud.api.player.extras;

import de.polocloud.api.util.MinecraftProtocol;

import java.net.InetSocketAddress;
import java.util.UUID;


public interface IPlayerConnection {

    /**
     * The host of this connection
     *
     * @return host as {@link String}
     */
    String getHost();

    /**
     * The name of this connection
     */
    String getName();

    /**
     * The {@link UUID} of this connection
     */
    UUID getUniqueId();

    /**
     * The port of this connection
     *
     * @return port as {@link Integer}
     */
    int getPort();

    /**
     * The protocol version of this connection
     *
     * @return version enum
     */
    MinecraftProtocol getVersion();

    /**
     * Gets this address as {@link InetSocketAddress}
     * by host and port
     *
     * @return socket address
     */
    InetSocketAddress getAddress();

    /**
     * Checks if this connection is online mode
     *
     * @return boolean
     */
    boolean isOnlineMode();

    /**
     * I'm not sure what this is
     *
     * @return boolean
     */
    boolean isLegacy();
}
