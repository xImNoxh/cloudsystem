package de.polocloud.api.bridge;

import de.polocloud.api.common.PoloType;

import java.util.UUID;

public interface PoloPluginBridge {

    /**
     * Checks if a player with a given {@link UUID} has a permission
     *
     * @param uniqueId the uuid of the player
     * @param permission the permission
     * @return if has permission
     */
    boolean hasPermission(UUID uniqueId, String permission);

    /**
     * Sends a message to a player with a given {@link UUID}
     *
     * @param uniqueId the uuid
     * @param message the message
     */
    void sendMessage(UUID uniqueId, String message);

    /**
     * Sets the tablist for a player with a given {@link UUID}
     *
     * @param uniqueId the uuid
     * @param header the header
     * @param footer the footer
     */
    void sendTabList(UUID uniqueId, String header, String footer);

    /**
     * Broadcasts a message
     *
     * @param message the message
     */
    void broadcast(String message);

    /**
     * Kicks a player from this bridge instance
     * For a given reason as {@link String}
     *
     * @param uniqueId the uuid of the player
     * @param reason the reason to display
     */
    void kickPlayer(UUID uniqueId, String reason);

    /**
     * Sends a title to a player with a given {@link UUID}
     *
     * @param uniqueId the uuid
     * @param title the title
     * @param subTitle the subtitle
     */
    void sendTitle(UUID uniqueId, String title, String subTitle);

    /**
     * Executes a console command
     *
     * @param command the command-line to execute
     */
    void executeCommand(String command);

    /**
     * The environment of this bridge instance
     */
    PoloType getEnvironment();

    /**
     * Displays a message above the hotbar of a player
     * with a given {@link UUID}
     *
     * @param uniqueId the uuid
     * @param message the message
     */
    void sendActionbar(UUID uniqueId, String message);

    /**
     * Shuts down this bridge
     */
    void shutdown();
}
