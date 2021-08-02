package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.bootstrap.Master;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

@CloudCommand.Info(name = "reload", commandType = CommandType.CONSOLE, aliases = "rl", description = "a cloud reload command")
public class CloudReloadCommand extends CloudCommand {

    @Override
    public void execute(ICommandExecutor sender, String[] args) {
        long start = System.currentTimeMillis();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Starting cloud reload...");
        Master.getInstance().getModuleCache().unloadModules();
        Master.getInstance().getModuleLoader().loadModules(true);

        Logger.log(LoggerType.INFO, Logger.PREFIX + "Cloud " + ConsoleColors.GREEN.getAnsiCode() + "complete "
            + ConsoleColors.GRAY.getAnsiCode() + "reload. (" + (System.currentTimeMillis() - start) + "ms)");

    }
}
