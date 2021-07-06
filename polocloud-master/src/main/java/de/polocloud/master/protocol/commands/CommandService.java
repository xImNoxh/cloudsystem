package de.polocloud.master.protocol.commands;

import de.polocloud.api.CloudAPI;
import de.polocloud.master.protocol.commands.executes.StopCommand;

public class CommandService {

    public CommandService() {
        CloudAPI.getInstance().getCommandPool().registerCommand(new StopCommand());
    }
}
