package de.polocloud.api.bridge;

import de.polocloud.api.chat.CloudComponent;
import de.polocloud.api.player.extras.IPlayerSettings;

import java.util.UUID;

public interface PoloPluginBungeeBridge extends PoloPluginBridge {

    /**
     * Connects a player with a given {@link UUID} to a server
     *
     * @param uniqueId the uuid
     * @param server the name of the server
     */
    void connect(UUID uniqueId, String server);

    /**
     * Sends a component to a player with a given {@link UUID}
     *
     * @param uuid the uuid
     * @param component the component
     */
    void sendComponent(UUID uuid, CloudComponent component);

    /**
     * Returns the {@link IPlayerSettings} of a player
     *
     * @param uniqueId the uuid of the player
     * @return settings
     */
    IPlayerSettings getSettings(UUID uniqueId);
}
