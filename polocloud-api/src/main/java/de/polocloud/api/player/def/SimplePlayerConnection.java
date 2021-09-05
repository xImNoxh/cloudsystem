package de.polocloud.api.player.def;


import de.polocloud.api.player.extras.IPlayerConnection;
import de.polocloud.api.util.MinecraftProtocol;
import de.polocloud.api.util.gson.PoloHelper;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Class is used
 * to disconnect the player
 * and send packets
 */
public class SimplePlayerConnection implements IPlayerConnection {


    /**
     * The UUId of this connection
     */
    private final UUID uniqueId;

    /**
     * The name of this connection
     */
    private final String name;

    /**
     * The address (host)
     */
    private final String host;

    /**
     * The address (port)
     */
    private final int port;

    /**
     * The protocolVersion
     */
    private final MinecraftProtocol version;

    /**
     * If the connection is online (Cracked users)
     */
    private final boolean onlineMode;

    /**
     * If its legacy or not
     */
    private final boolean legacy;

    public SimplePlayerConnection(UUID uniqueId, String name, String host, int port, MinecraftProtocol version, boolean onlineMode, boolean legacy) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.host = host;
        this.port = port;
        this.version = version;
        this.onlineMode = onlineMode;
        this.legacy = legacy;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public MinecraftProtocol getVersion() {
        return version;
    }

    @Override
    public boolean isOnlineMode() {
        return onlineMode;
    }

    @Override
    public boolean isLegacy() {
        return legacy;
    }

    @Override
    public InetSocketAddress getAddress() {
        return new InetSocketAddress(this.host, this.port);
    }

    @Override
    public String toString() {
        return PoloHelper.GSON_INSTANCE.toJson(this);
    }

}
