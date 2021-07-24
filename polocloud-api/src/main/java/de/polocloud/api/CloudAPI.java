package de.polocloud.api;

import com.google.inject.Injector;
import de.polocloud.api.commands.ICommandPool;
import de.polocloud.api.commands.ICommandExecutor;

public abstract class CloudAPI {

    protected static CloudAPI instance;

    public CloudAPI() {
        instance = this;
    }

    public abstract ICommandPool getCommandPool();

    public abstract Injector getGuice();

    public abstract ICommandExecutor getConsoleExecutor();

    public static CloudAPI getInstance() {
        return instance;
    }
}
