package de.polocloud.api;

import com.google.inject.Injector;
import de.polocloud.api.commands.CommandPool;

public abstract class CloudAPI {

    private static CloudAPI instance;

    public CloudAPI() {
        instance = this;
    }

    public abstract CommandPool getCommandPool();

    public abstract Injector getGuice();

    public static CloudAPI getInstance() {
        return instance;
    }
}
