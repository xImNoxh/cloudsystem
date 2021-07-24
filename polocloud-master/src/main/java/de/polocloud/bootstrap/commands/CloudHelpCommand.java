package de.polocloud.bootstrap.commands;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

@CloudCommand.Info(name = "help", description = "", aliases = "", commandType = CommandType.CONSOLE)
public class CloudHelpCommand extends CloudCommand {

    @Override
    public void execute(ICommandExecutor commandSender, String[] args) {
        CloudAPI.getInstance().getCommandPool().getAllCachedCommands().forEach(key ->
            Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.CYAN.getAnsiCode() + key.getName() + ConsoleColors.GRAY.getAnsiCode() +
                " » " + ConsoleColors.GRAY.getAnsiCode() + key.getDescription()));
        Logger.newLine();
        Logger.log(LoggerType.INFO, ConsoleColors.GRAY.getAnsiCode() + "Founded " + CloudAPI.getInstance().getCommandPool().getAllCachedCommands().size()
            + " commands in the cloud.");
        Logger.newLine();
    }
}
