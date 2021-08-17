package de.polocloud.wrapper.commands;

import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.wrapper.Wrapper;

public class StopCommand implements CommandListener {

    @Command(name = "stop", aliases = "asdwad", description = "wadawdaw")
    public void execute(CommandExecutor executor, String[] args) {
        Logger.log(LoggerType.INFO, Logger.PREFIX + "stopping...");
        Wrapper.getInstance().terminate();
    }
}
