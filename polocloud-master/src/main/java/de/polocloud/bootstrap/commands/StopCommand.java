package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;

@CloudCommand.Info(name = "stop", description = "Stops the cloud", aliases = "exit", commandType = CommandType.CONSOLE)
public class StopCommand extends CloudCommand {
    @Override
    public void execute(ICommandExecutor sender, String[] args) {
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Stopping...");
        System.exit(0);
    }
}
