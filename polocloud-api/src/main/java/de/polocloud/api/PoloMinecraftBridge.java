package de.polocloud.api;

import java.util.UUID;

public interface PoloMinecraftBridge {

    /**
     * Checks if a player with a given {@link UUID} has a permission
     *
     * @param uniqueId the uuid of the player
     * @param permission the permission
     * @return if has permission
     */
    boolean hasPermission(UUID uniqueId, String permission);

    /**
     * Shuts down this bridge
     */
    void shutdown();
}
