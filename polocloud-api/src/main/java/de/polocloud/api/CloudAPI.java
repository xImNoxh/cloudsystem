package de.polocloud.api;

import de.polocloud.api.commands.CommandPool;

public abstract class CloudAPI {

    private static CloudAPI instance;

    public static CloudAPI getInstance() {
        return instance;
    }

    public CloudAPI() {
        instance = this;
    }

    public abstract CommandPool getCommandPool();

}
