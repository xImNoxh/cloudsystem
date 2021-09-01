package de.polocloud.plugin.bootstrap;

import de.polocloud.api.bridge.PoloPluginBridge;

public interface IBootstrap {

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
     * The bridge instance
     */
    PoloPluginBridge getBridge();


}
