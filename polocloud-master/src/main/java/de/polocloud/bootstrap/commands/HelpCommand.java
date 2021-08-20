package de.polocloud.bootstrap.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.command.runner.ICommandRunner;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HelpCommand implements CommandListener {

    @Command(
        name = "help",
        description = "Shows all available commands",
        usage = "help <no-args>",
        aliases = "?"
    )
    public void execute(CommandExecutor sender, String[] rawArgs) {
        Logger.newLine();

        for (ICommandRunner command : PoloCloudAPI.getInstance().getCommandManager().getCommands().stream().filter(command -> Arrays.asList(command.getAllowedSourceTypes()).contains(ExecutorType.CONSOLE) || Arrays.asList(command.getAllowedSourceTypes()).contains(ExecutorType.ALL)).collect(Collectors.toList())) {
                Logger.log(LoggerType.INFO, Logger.PREFIX + "Command: " + ConsoleColors.LIGHT_BLUE + command.getCommand().name() + ConsoleColors.GRAY
                    + "(" + ConsoleColors.LIGHT_BLUE + String.join(ConsoleColors.GRAY + ", " + ConsoleColors.LIGHT_BLUE, command.getCommand().aliases()) + ConsoleColors.GRAY + ") × " + command.getCommand().description());
        }
        Logger.newLine();
    }
}
