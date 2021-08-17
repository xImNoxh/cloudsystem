package de.polocloud.wrapper.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.commands.CloudCommandInfo;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.wrapper.Wrapper;

@CloudCommandInfo(name = "stop", aliases = "asdwad", description = "wadawdaw", commandType = CommandType.CONSOLE)
public class StopCommand extends CloudCommand {

    @Override
    public void execute(ICommandExecutor executor, String[] args) {
        Logger.log(LoggerType.INFO, Logger.PREFIX + "stopping...");
        Wrapper.getInstance().terminate();
    }
}
