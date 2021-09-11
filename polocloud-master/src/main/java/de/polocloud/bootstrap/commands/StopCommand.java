package de.polocloud.bootstrap.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.annotation.CommandExecutors;
import de.polocloud.api.command.annotation.CommandPermission;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.bootstrap.Master;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;

public class StopCommand implements CommandListener {

    @Command(
        name = "stop",
        aliases = {"exit", "shutdown"},
        description = "Stops the cloud",
        usage = "stop <no-args>"
    )
    @CommandPermission("cloud.stop")
    @CommandExecutors(ExecutorType.CONSOLE)
    public void execute(CommandExecutor executor, String[] args) {
        PoloLogger.print(LogLevel.INFO, "Stopping...");
        PoloCloudAPI.getInstance().terminate();
    }
}
