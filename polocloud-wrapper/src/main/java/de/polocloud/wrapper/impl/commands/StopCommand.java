package de.polocloud.wrapper.impl.commands;

import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.wrapper.Wrapper;

public class StopCommand implements CommandListener {

    @Command(name = "stop", aliases = "exit", description = "Stops the wrapper")
    public void execute(CommandExecutor executor, String[] args) {
        PoloLogger.print(LogLevel.INFO, "stopping...");
        Wrapper.getInstance().terminate();
    }
}
