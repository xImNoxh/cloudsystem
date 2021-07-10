package de.polocloud.bootstrap.commands;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.commands.CloudCommand;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

@CloudCommand.Info(name = "help", description = "", aliases = "")
public class HelpCommand extends CloudCommand {

    @Override
    public void execute(String[] args) {
        CloudAPI.getInstance().getCommandPool().getAllCachedCommands().forEach(key ->
            Logger.log(LoggerType.INFO, ConsoleColors.LIGHT_BLUE.getAnsiCode() + "PoloCloud "+ ConsoleColors.GRAY.getAnsiCode() + "» " + ConsoleColors.CYAN.getAnsiCode() + key.getName() + ConsoleColors.GRAY.getAnsiCode() +
                " » " + ConsoleColors.GRAY.getAnsiCode() + key.getDescription()));
        Logger.newLine();
        Logger.log(LoggerType.INFO, ConsoleColors.GRAY.getAnsiCode() + "Founded " + CloudAPI.getInstance().getCommandPool().getAllCachedCommands().size()
            + " commands in the cloud.");
        Logger.newLine();
    }
}
