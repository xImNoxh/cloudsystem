package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.bootstrap.Master;

@CloudCommand.Info(name = "reload", commandType = CommandType.CONSOLE, aliases = "rl", description = "a cloud reload command")
public class CloudReloadCommand extends CloudCommand {

    @Override
    public void execute(ICommandExecutor sender, String[] args) {
        Master.getInstance().getModuleCache().unloadModules();
        Master.getInstance().getModuleLoader().loadModules();
    }
}
