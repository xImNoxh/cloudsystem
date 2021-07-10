package de.polocloud.bootstrap.commands;


import de.polocloud.api.commands.CloudCommand;

@CloudCommand.Info(
    name = "stop",
    description = "stop the server",
    aliases = ""
)
public class ClouldStopCommand extends CloudCommand {

    @Override
    public void execute(String[] args) {
        System.exit(-1);
    }
}
