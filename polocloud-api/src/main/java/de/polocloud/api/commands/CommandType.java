package de.polocloud.api.commands;

public enum CommandType {

    /**
     * The cloud-console executed
     */
    CONSOLE,

    /**
     * The (spigot||proxy)-console executed
     */
    INGAME_CONSOLE,

    /**
     * A player executed
     */
    INGAME;

}
