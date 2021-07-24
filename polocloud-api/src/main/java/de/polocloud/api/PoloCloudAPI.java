package de.polocloud.api;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.polocloud.api.commands.CommandPool;
import de.polocloud.api.commands.ICommandPool;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.commands.types.ConsoleExecutor;

public class PoloCloudAPI extends CloudAPI {

    private Injector inector;
    private ICommandPool commandPool;

    private ICommandExecutor commandSender;

    public PoloCloudAPI(AbstractModule... modules) {
        instance = this;
        this.inector = Guice.createInjector(modules);
        commandPool = new CommandPool();

        this.commandSender = new ConsoleExecutor();
    }

    @Override
    public ICommandPool getCommandPool() {
        return commandPool;
    }

    @Override
    public Injector getGuice() {
        return this.inector;
    }

    @Override
    public ICommandExecutor getConsoleExecutor() {
        return commandSender;
    }
}
