package de.polocloud.api.bridge;

import java.util.UUID;

public interface PoloPluginBungeeBridge extends PoloPluginBridge {

    /**
     * Connects a player with a given {@link UUID} to a server
     *
     * @param uniqueId the uuid
     * @param server the name of the server
     */
    void connect(UUID uniqueId, String server);
}
