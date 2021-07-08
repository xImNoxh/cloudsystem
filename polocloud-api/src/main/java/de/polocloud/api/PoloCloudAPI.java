package de.polocloud.api;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.polocloud.api.commands.CommandPool;
import de.polocloud.api.guice.PoloAPIGuiceModule;

public class PoloCloudAPI extends CloudAPI {

    private Injector inector;

    public PoloCloudAPI(AbstractModule... modules) {
        this.inector = Guice.createInjector(modules);
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
