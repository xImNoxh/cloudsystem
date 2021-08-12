package de.polocloud.bootstrap.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.bootstrap.Master;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.Arrays;

@CloudCommand.Info(name = "Help", description = "Shows all available commands", aliases = "?", commandType = CommandType.CONSOLE)
public class HelpCommand extends CloudCommand {

    @Override
    public void execute(ICommandExecutor sender, String[] args) {
        Logger.newLine();
        for (CloudCommand command : PoloCloudAPI.getInstance().getCommandPool().getAllCachedCommands()) {
            Logger.log(LoggerType.INFO, Logger.PREFIX + "Command: " + ConsoleColors.LIGHT_BLUE + command.getName() + ConsoleColors.GRAY
                + "(" + ConsoleColors.LIGHT_BLUE + String.join(ConsoleColors.GRAY + ", " + ConsoleColors.LIGHT_BLUE, command.getAliases()) + ConsoleColors.GRAY + ") × " + command.getDescription());
        }
        Logger.newLine();
    }
}
