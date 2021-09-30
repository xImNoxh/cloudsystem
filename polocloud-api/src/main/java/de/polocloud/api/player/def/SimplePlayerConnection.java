package de.polocloud.api.player.def;


import de.polocloud.api.player.extras.IPlayerConnection;
import de.polocloud.api.gameserver.helper.MinecraftProtocol;
import de.polocloud.api.util.PoloHelper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Class is used
 * to disconnect the player
 * and send packets
 */
@Data @AllArgsConstructor
public class SimplePlayerConnection implements IPlayerConnection {

    /**
     * The address
     */
    private final InetSocketAddress address;

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
    private String host;

    /**
     * The address (port)
     */
    private int port;

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

    @Override
    public InetSocketAddress constructAddress() {
        return new InetSocketAddress(this.host, this.port);
    }

    @Override
    public void injectAddress(InetSocketAddress address) {
        this.port = address.getPort();
        this.host = address.getAddress().getHostAddress();
    }

    @Override
    public String toString() {
        return PoloHelper.GSON_INSTANCE.toJson(this);
    }

}
