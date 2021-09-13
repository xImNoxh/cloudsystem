package de.polocloud.wrapper.impl.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.command.runner.ICommandRunner;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.console.ConsoleColors;
import de.polocloud.api.logger.helper.LogLevel;

public class HelpCommand implements CommandListener {

    @Command(
        name = "help",
        description = "Shows all available commands",
        usage = "help <no-args>",
        aliases = "?"
    )
    public void execute(CommandExecutor sender, String[] rawArgs) {

        for (ICommandRunner command : PoloCloudAPI.getInstance().getCommandManager().getCommands()) {
            PoloLogger.print(LogLevel.INFO, "Command: " + ConsoleColors.LIGHT_BLUE + command.getCommand().name() + ConsoleColors.GRAY
                + "(" + ConsoleColors.LIGHT_BLUE + String.join(ConsoleColors.GRAY + ", " + ConsoleColors.LIGHT_BLUE, command.getCommand().aliases()) + ConsoleColors.GRAY + ") × " + command.getCommand().description());
        }
    }
}
