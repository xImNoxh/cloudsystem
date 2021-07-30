package de.polocloud.bootstrap.commands;


import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;

@CloudCommand.Info(
    name = "stop",
    description = "stop the server",
    aliases = "", commandType = CommandType.CONSOLE
)
public class CloudStopCommand extends CloudCommand {

    @Override
    public void execute(ICommandExecutor commandSender, String[] args) {
        System.exit(-1);
    }
}
