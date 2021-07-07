package de.polocloud.api;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.polocloud.api.commands.CommandPool;
import de.polocloud.api.guice.PoloGuiceModule;

public class PoloCloudAPI extends CloudAPI {

    private Injector inector;

    public PoloCloudAPI() {
        this.inector = Guice.createInjector(new PoloGuiceModule());
    }

    @Override
    public CommandPool getCommandPool() {
        return null;
    }

    @Override
    public Injector getGuice() {
        return this.inector;
    }
}
