package de.polocloud.wrapper.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;

@CloudCommand.Info(name = "stop", aliases = "asdwad", description = "wadawdaw", commandType = CommandType.CONSOLE)
public class StopCommand extends CloudCommand {

    @Override
    public void execute(ICommandExecutor executor, String[] args) {
        System.exit(-1);
    }
}
