package de.polocloud.api.common;

public enum PoloType {

    /**
     * The master process
     */
    MASTER,

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
    GENERAL_GAMESERVER;

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
     * Checks if this enum-type
     * is instance of spigot or proxy to
     * check if it's a plugin-instance
     *
     * @return boolean
     */
    public boolean isPlugin() {
        return this == PLUGIN_SPIGOT || this == PLUGIN_PROXY;
    }
}
