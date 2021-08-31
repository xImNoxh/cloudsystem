package de.polocloud.plugin.bootstrap;

import de.polocloud.api.common.PoloType;

import java.util.UUID;

public interface IBootstrap {

    /**
     * Shuts down this bootstrap instance
     */
    void shutdown();

    /**
     * The port of the bootstrap
     * e.g. proxy port or bukkit port
     */
    int getPort();

    /**
     * Registers all listeners
     */
    void registerListeners();

    /**
     * Registers all important
     * and default packet listeners
     */
    void registerPacketListening();

    /**
     * The type of the bootstrap
     * to identify if proxy or spigot
     */
    PoloType getType();

    /**
     * Kicks a player from the server or proxy
     *
     * @param uuid the uuid of the player
     * @param message the reason for the kick
     */
    void kick(UUID uuid, String message);

    /**
     * Executes a console command
     *
     * @param command the command-line to execute
     */
    void executeCommand(String command);


}
