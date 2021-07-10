package de.polocloud.wrapper.commands;

import de.polocloud.api.commands.CloudCommand;

@CloudCommand.Info(name = "stop", aliases = "asdwad", description = "wadawdaw")
public class StopCommand extends CloudCommand {

    @Override
    public void execute(String[] args) {
        System.exit(-1);
    }
}
