package de.polocloud.api.common;

public enum PoloType {

    /**
     * The master process
     */
    MASTER,

    /**
     * The master and wrapper process
     */
    MASTER_AND_WRAPPER,

    /**
     * The wrapper process
     */
    WRAPPER,

    /**
     * Spigot-Bridge
     */
    PLUGIN_SPIGOT,

    /**
     * Proxy-Bridge
     */
    PLUGIN_PROXY,

    /**
     * GameServer instance
     */
    GENERAL_GAMESERVER,

    /**
     * This should not happen to appear
     */
    NONE;

    /**
     * Checks if this enum-type
     * is instance of master or wrapper to
     * check if it's a global-cloud-instance
     *
     * @return boolean
     */
    public boolean isCloud() {
        return this == MASTER || this == WRAPPER;
    }

    /**
     * The display name formatted
     * where the first letter is uppercase
     */
    public String getName() {
        return name().toUpperCase().charAt(0) + name().substring(1).toLowerCase();
    }

    /**
     * Checks if this enum-type
     * is instance of spigot or proxy to
     * check if it's a plugin-instance
     *
     * @return boolean
     */
    public boolean isPlugin() {
        return this == PLUGIN_SPIGOT || this == PLUGIN_PROXY || this == GENERAL_GAMESERVER;
    }
}
