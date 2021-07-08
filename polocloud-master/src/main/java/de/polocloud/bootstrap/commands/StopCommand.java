package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;

@CloudCommand.Info(name = "stop", aliases = "", description = "")
public class StopCommand extends CloudCommand {

    @Override
    public void execute(String[] args) {
        System.exit(0);
    }
}
