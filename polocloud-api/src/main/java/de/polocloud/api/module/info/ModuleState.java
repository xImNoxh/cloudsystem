package de.polocloud.api.module.info;

public enum ModuleState {

    /**
     * Module is being loaded
     */
    LOADING,

    /**
     * Module is being enabled
     */
    STARTING,

    /**
     * Module is stopping
     */
    STOPPING,

    /**
     * Module is reloading
     */
    RELOADING
}
