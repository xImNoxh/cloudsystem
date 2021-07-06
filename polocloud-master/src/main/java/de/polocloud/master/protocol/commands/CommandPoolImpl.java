package de.polocloud.master.protocol.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandPool;

import java.util.ArrayList;
import java.util.List;

public class CommandPoolImpl implements CommandPool {

    private List<CloudCommand> commands = new ArrayList<>();

    @Override
    public void registerCommand(CloudCommand cloudCommand) {
        commands.add(cloudCommand);
    }

    @Override
    public List<CloudCommand> getAllCachedCommands() {
        return commands;
    }

    @Override
    public void unregisterCommand(CloudCommand cloudCommand) {
        commands.remove(cloudCommand);
    }

    public List<CloudCommand> getCommands() {
        return commands;
    }
}

